package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.exception.ai.NullAiResponse
import com.yourRPG.chatPG.exception.message.InvalidMessageReferenceIdException
import com.yourRPG.chatPG.exception.message.MessageNotFoundException
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Message
import com.yourRPG.chatPG.repository.MessageRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.chat.ChatService
import com.yourRPG.chatPG.validator.message.MessageContentValidator
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min

@Service
class MessageService(
    /* Service */
    private val accountService: AccountService,
    private val chatService: ChatService,
    private val chatPGService: ChatpgService,
    private val aiService: AiService,

    /* Repositories */
    private val repository: MessageRepository,

    /* Validators */
    private val contentValidator: MessageContentValidator
): IConvertible<Message, MessageDto> {

    /**
     * Conversion.
     *
     * @see IConvertible
     */
    override fun dtoOf(c: Message): MessageDto = MessageDto(c)

    /**
     * Fetches 20 messages in the [Chat] identified with [accountId] and [chatId]. The messages are fetched by selecting
     *  the ids lesser than the [referenceId], and therefore, older messages than the reference.
     * If the [referenceId] equals -1, then it fetches using a [Long.MAX_VALUE], which makes
     *  [MessageRepository.qFindOldByChatIdAndReference] provide the last 20 messages of the chat, since they just need
     *  to have lesser ids than the largest possible.
     *
     * @param accountId identifier of account
     * @param chatId identifier of chat
     * @param referenceId reference for fetching
     * @return [List] of [MessageDto]s fetched
     * @see ChatService.validateAccess
     * @see MessageRepository.qFindOldByChatIdAndReference
     */
    fun getOldMessagesInChat(accountId: Long, chatId: Long, referenceId: Long): List<MessageDto> =
        chatService.validateAccess(accountId, chatId).run {
            require(referenceId >= -1L) {
                "reference id must be >= 0 or -1"
            }

            return when(referenceId) {
                -1L -> repository.qFindOldByChatIdAndReference(chatId, reference = Long.MAX_VALUE).toListDto()
                else -> repository.qFindOldByChatIdAndReference(chatId, referenceId).toListDto()
            }
        }

    /**
     * Fetches 20 messages in the [Chat] identified with [accountId] and [chatId]. The messages are fetched by selecting
     *  the ids greater than the [referenceId], and therefore, newer messages than the reference.
     *
     * @param accountId identifier of account
     * @param chatId identifier of chat.
     * @param referenceId reference for fetching.
     * @see ChatService.validateAccess
     * @see MessageRepository.qFindNewByChatIdAndReference
     */
    fun getNewMessagesInChat(accountId: Long, chatId: Long, referenceId: Long): List<MessageDto> {
        chatService.validateAccess(accountId, chatId).run {
            if (referenceId < 0) {
                throw InvalidMessageReferenceIdException("Valid reference required. Reference given: $referenceId")
            }

            return repository.qFindNewByChatIdAndReference(chatId, referenceId).toListDto()
        }
    }

    /**
     * Fetches a message with id [messageId] in the [Chat] identified with [chatId].
     *
     * @param accountId account identifier
     * @return [Message] found
     * @throws MessageNotFoundException if no message with id [messageId] was found in the chat with id [chatId]
     * @see ChatService.validateAccess
     * @see MessageRepository.findByChatIdAndId
     */
    fun getByChatIdAndId(accountId: Long, chatId: Long, messageId: Long): Message =
        chatService.validateAccess(accountId, chatId).run {
            return repository.findByChatIdAndId(chatId, messageId)
                ?: throw MessageNotFoundException("Unable to find message with id: $messageId")
        }

    fun getDtoByChatIdAndId(accountId: Long, chatId: Long, messageId: Long): MessageDto =
        getByChatIdAndId(accountId, chatId, messageId).toDto()

    /**
     * Creates a message as the account identified by [accountId] in the chat identified by [chatId] with content [content]
     *
     * @param accountId account identifier.
     * @param chatId chat identifier.
     * @param content content of the message.
     * @return [MessageDto] of the message created.
     * @see AccountService.getById
     * @see ChatService.getByAccountIdAndChatId
     * @see createMessage
     */
    fun sendMessage(accountId: Long, chatId: Long, content: String): MessageDto =
        createMessage(
            accountService.getById(accountId),
            chatService.getByAccountIdAndChatId(accountId, chatId),
            content,
            isBot = false
        ).toDto()

    /**
     * Validates content of message, constructs the [Message], then persists it.
     *
     * @param account who sent the message (can be null if a bot).
     * @param chat chat where the message is gonna be assigned to under creation.
     * @param content message content.
     * @param isBot whether the message was sent by a bot or not
     * @see MessageContentValidator.validate
     */
    private fun createMessage(account: Account?, chat: Chat, content: String, isBot: Boolean): Message =
        contentValidator.validate(t = content).run {
            repository.save(Message(account, chat, content, isBot))
        }

    /**
     * Generates an AI message on a chat identified by [chatId].
     *
     * @param accountId account identifier for access validation
     * @param chatId chat identifier
     * @see ChatService.getByAccountIdAndChatId
     * @see createAIMessage
     */
    fun generateResponse(accountId: Long, chatId: Long): MessageDto =
        createAIMessage(
            chatService.getByAccountIdAndChatId(accountId, chatId)
        )

    /**
     * Fetches previous messages present in the chat ([MessageRepository.qFindAllMessagesFromChat]), then treats them
     *  ([ChatpgService.treatMemoryForPrompt]) to use as context for the generation of a new message. Requests the
     *  generation of a message with [AiService.askAi].
     *
     * @param chat
     * @return [MessageDto] of message created
     * @throws NullAiResponse if response from the request of text generation was null.
     * @throws com.yourRPG.chatPG.exception.ai.models.UnavailableAiException if there was a problem with the response
     *  given by the provider's side
     * @see MessageRepository.qFindAllMessagesFromChat
     * @see ChatpgService.treatMemoryForPrompt
     * @see AiService.askAi
     * @see createMessage
     */
    private fun createAIMessage(chat: Chat): MessageDto {

        val messages = repository.qFindAllMessagesFromChat(chat)

        val prompt = chatPGService.treatMemoryForPrompt(messages)

        val content = aiService.askAi(chat.model, prompt)
            ?: throw NullAiResponse("Response from ${chat.model.nickname} was null")

        return createMessage(
            account = null,
            chat,
            content,
            isBot = true
        ).toDto()
    }

    /**
     * Deletes a single message in the chat identified by [chatId] with validation of [ChatService.validateAccess] to
     *  ensure that the account requesting does have access to the chat. The message
     *  deleted is identified by its [messageId].
     *
     * @param accountId account identifier
     * @param chatId chat identifier
     * @param messageId message identifier
     * @see ChatService.validateAccess
     * @throws MessageNotFoundException if no messages where deleted
     */
    @Transactional
    fun deleteMessage(accountId: Long, chatId: Long, messageId: Long) =
        chatService.validateAccess(accountId, chatId).run {
            if (repository.qDeleteByChatIdAndId(chatId, messageId) == 0)
                throw MessageNotFoundException("No messages deleted")
        }

    /**
     * Deletes a multiple messages in the chat identified by [chatId] with validation of [ChatService.validateAccess] to
     *  ensure that the account requesting does have access to the chat. The messages deleted are the ones within the
     *  inclusive range of ids with bounds [bound1] and [bound2]. To enable any order of upper bound and lower bound,
     *  there is a check to pass the idStart and idFinish as the smallest and greater between the bounds respectively.
     *
     * @param accountId account identifier
     * @param chatId chat identifier
     * @param bound1 one of the range's bounds
     * @param bound2 another of the range's bounds
     * @throws MessageNotFoundException if no messages where deleted
     * @see ChatService.validateAccess
     */
    @Transactional
    fun bulkDeleteMessages(accountId: Long, chatId: Long, bound1: Long, bound2: Long) =
        chatService.validateAccess(accountId, chatId).run {
            val idStart = min(bound1, bound2)
            val idFinish = max(bound1, bound2)

            if (repository.qBulkDeleteByChatIdFromIdToId(chatId, idStart, idFinish) == 0)
                throw MessageNotFoundException("No messages deleted")

        }

}