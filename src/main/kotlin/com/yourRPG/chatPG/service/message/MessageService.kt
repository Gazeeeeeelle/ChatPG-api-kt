package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.exception.ai.NullAiResponse
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
     * Fetches 20 messages in the [Chat] identified with and [chatId]. The messages are fetched by selecting
     *  the ids lesser than the [referenceId], and therefore, older messages than the reference.
     * If the [referenceId] equals -1, then it fetches using a [Long.MAX_VALUE], which makes
     *  [MessageRepository.qFindOldByChatIdAndReference] provide the last 20 messages of the chat, since they just need
     *  to have lesser ids than the largest possible.
     *
     * @param chatId identifier of chat
     * @param referenceId reference for fetching
     * @return [List] of [MessageDto]s fetched
     * @see MessageRepository.qFindOldByChatIdAndReference
     */
    fun getOldMessagesInChat(chatId: Long, referenceId: Long): List<MessageDto> {
        require(referenceId >= -1L) { "Invalid reference ID: $referenceId" }

        return when (referenceId) {
            -1L -> repository.qFindOldByChatIdAndReference(chatId, reference = Long.MAX_VALUE).toListDto()
            else -> repository.qFindOldByChatIdAndReference(chatId, referenceId).toListDto()
        }
    }
    /**
     * Fetches 20 messages in the [Chat] identified with [chatId]. The messages are fetched by selecting
     *  the ids greater than the [referenceId], and therefore, newer messages than the reference.
     *
     * @param chatId identifier of chat.
     * @param referenceId reference for fetching.
     * @see MessageRepository.qFindNewByChatIdAndReference
     */
    fun getNewMessagesInChat(chatId: Long, referenceId: Long): List<MessageDto> {
        require(referenceId >= -1L) { "Invalid reference ID: $referenceId" }

        return repository.qFindNewByChatIdAndReference(chatId, referenceId).toListDto()
    }

    /**
     * Fetches a message with id [messageId] in the [Chat] identified with [chatId].
     *
     * @return [Message] found
     * @throws MessageNotFoundException if no message with id [messageId] was found in the chat with id [chatId]
     * @see MessageRepository.findByChatIdAndId
     */
    fun getByChatIdAndId(chatId: Long, messageId: Long): Message =
        repository.findByChatIdAndId(chatId, messageId)
            ?: throw MessageNotFoundException("Unable to find message with id: $messageId")

    /**
     * Delegates to [getByChatIdAndId], then converts to DTO.
     *
     * @param chatId
     * @param messageId
     */
    fun getDtoByChatIdAndId(chatId: Long, messageId: Long): MessageDto =
        getByChatIdAndId(chatId, messageId).toDto()

    /**
     * Creates a message as the account identified by [accountId] in the chat identified by [chatId] with content [content]
     *
     * @param accountId account identifier.
     * @param chatId chat identifier.
     * @param content content of the message.
     * @return [MessageDto] of the message created.
     * @see AccountService.getById
     * @see ChatService.getByChatId
     * @see createMessage
     */
    fun sendMessage(accountId: Long, chatId: Long, content: String): MessageDto =
        createMessage(
            accountService.getById(accountId),
            chatService.getByChatId(chatId),
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
     * @param chatId chat identifier
     * @see ChatService.getByChatId
     * @see createAIMessage
     */
    fun generateResponse(chatId: Long): MessageDto =
        createAIMessage(
            chatService.getByChatId(chatId)
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
    fun createAIMessage(chat: Chat): MessageDto {

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
     * Deletes a single message in the chat identified by [chatId]. The message deleted is identified by its [messageId].
     *
     * @param chatId chat identifier
     * @param messageId message identifier
     * @throws MessageNotFoundException if no messages where deleted
     */
    @Transactional
    fun deleteMessage(chatId: Long, messageId: Long) =
        repository.qDeleteByChatIdAndId(chatId, messageId).let {
            if (it == 0) throw MessageNotFoundException("Message with id $messageId not found")
        }

    /**
     * Deletes a multiple messages in the chat identified by [chatId]. The messages deleted are the ones within the
     *  inclusive range of ids with bounds [bound1] and [bound2]. To enable any order of upper bound and lower bound,
     *  there is a check to pass the idStart and idFinish as the smallest and greater between the bounds respectively.
     *
     * @param chatId chat identifier
     * @param bound1 one of the range's bounds
     * @param bound2 another of the range's bounds
     * @throws MessageNotFoundException if no messages where deleted
     */
    @Transactional
    fun bulkDeleteMessages(chatId: Long, bound1: Long, bound2: Long) {
        val (idStart, idFinish) =
            min(a = bound1, b = bound2) to max(a = bound1, b = bound2)

        repository.qBulkDeleteByChatIdFromIdToId(chatId, idStart, idFinish).let {
            if (it == 0) throw MessageNotFoundException("No messages deleted")
        }
    }

}