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
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     * @see ChatService.validateAccess
     * @see MessageRepository.qFindOldByChatIdAndReference
     */
    fun getOldMessagesInChat(accountId: Long, chatId: Long, referenceId: Long): List<MessageDto> {
        chatService.validateAccess(Pair(accountId, chatId))

        return if (referenceId >= 0L) {
            repository.qFindOldByChatIdAndReference(chatId, referenceId).toListDto()
        } else if (referenceId == -1L) {
            repository.qFindOldByChatIdAndReference(chatId, reference = Long.MAX_VALUE).toListDto()
        } else {
            throw InvalidMessageReferenceIdException("Valid reference required. Reference given: $referenceId")
        }
    }

    /**
     * Fetches 20 messages in the [Chat] identified with [accountId] and [chatId]. The messages are fetched by selecting
     *  the ids greater than the [referenceId], and therefore, newer messages than the reference.
     *
     * @param accountId identifier of account, used to assure that the [Account] requesting the messages has access to
     *  the chat.
     * @param chatId identifier of chat.
     * @param referenceId reference for fetching.
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     * @see ChatService.validateAccess
     * @see MessageRepository.qFindNewByChatIdAndReference
     */
    fun getNewMessagesInChat(accountId: Long, chatId: Long, referenceId: Long): List<MessageDto> {
        chatService.validateAccess(Pair(accountId, chatId))

        if (referenceId < 0) {
            throw InvalidMessageReferenceIdException("Valid reference required. Reference given: $referenceId")
        }

        return repository.qFindNewByChatIdAndReference(chatId, referenceId).toListDto()
    }

    /**
     * Creates a message as the account identified by [accountId] in the chat identified by [chatId] with content [message]
     *
     * @param accountId account identifier.
     * @param chatId chat identifier.
     * @param message content of the message.
     * @return [MessageDto] of the message created.
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     * @see createMessage
     */
    fun sendMessage(accountId: Long, chatId: Long, message: String): MessageDto {
        val account = accountService.getById(accountId)

        val chat = chatService.getByAccountIdAndChatId(accountId, chatId)

        return createMessage(account, chat, message, false).toDto()
    }

    /**
     * Receives nullable [Account], [Chat], message content ([String]) and [Boolean] based on whether it is a bot message.
     *
     * @param account which can be null in case it is a bot message.
     * @param chat is the chat where the message is gonna be assigned to under creation.
     * @param message message content.
     * @param isBot
     * @throws com.yourRPG.chatPG.exception.message.MessageContentNotFoundException if the String was null.
     * @throws com.yourRPG.chatPG.exception.message.MessageContentBlankException if the String was blank.
     * @see MessageContentValidator.validate
     */
    private fun createMessage(account: Account?, chat: Chat, message: String, isBot: Boolean): Message {
        contentValidator.validate(t = message)

        val msg = Message(account, chat, content = message, isBot)

        repository.save(msg)

        return msg
    }

    /**
     * Generates an AI message on a chat identified by [chatId].
     *
     * @param accountId account identifier for access validation
     * @param chatId chat identifier
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     * @see createAIMessage
     */
    fun generateResponse(accountId: Long, chatId: Long): MessageDto {
        val chat: Chat = chatService.getByAccountIdAndChatId(accountId, chatId)

        return createAIMessage(chat)
    }

    /**
     * Fetches previous messages present in the chat ([MessageRepository.qFindAllMessagesFromChat]), then treats them
     *  ([ChatpgService.treatMemoryForPrompt]) to use as context for the generation of a new message. Requests the
     *  generation of a message with [AiService.askAi].
     *
     * @param chat
     * @return [MessageDto] of message created
     * @throws NullAiResponse if response from the request of text generation was null.
     * @throws com.yourRPG.chatPG.exception.ai.models.UnavailableAiException
     * @see MessageRepository.qFindAllMessagesFromChat
     * @see ChatpgService.treatMemoryForPrompt
     * @see AiService.askAi
     * @see createMessage
     */
    private fun createAIMessage(chat: Chat): MessageDto {
        val previousMessages: List<Message> = repository.qFindAllMessagesFromChat(chat)

        val memoryPrompt: String = chatPGService.treatMemoryForPrompt(previousMessages)

        val aiResponse: String = aiService.askAi(chat.model, memoryPrompt)
            ?: throw NullAiResponse("Response from ${chat.model} was null")

        return createMessage(account = null, chat, message = aiResponse, true).toDto()
    }

    /**
     * Deletes a single message in the chat identified by [chatId] with validation of [ChatService.validateAccess] to
     *  ensure that the account requesting does have access to the chat. The message
     *  deleted is identified by its [messageId].
     *
     * @param accountId account identifier
     * @param chatId chat identifier
     * @param messageId message identifier
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     * @throws MessageNotFoundException if no messages where deleted
     */
    @Transactional
    fun deleteMessage(accountId: Long, chatId: Long, messageId: Long) {
        chatService.validateAccess(Pair(accountId, chatId))

        if (repository.qDeleteByChatIdAndId(chatId, messageId) == 0) {
            throw MessageNotFoundException("No messages deleted")
        }
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
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     * @throws MessageNotFoundException if no messages where deleted
     */
    @Transactional
    fun bulkDeleteMessages(accountId: Long, chatId: Long, bound1: Long, bound2: Long) {
        chatService.validateAccess(Pair(accountId, chatId))

        val idStart = min(bound1, bound2)
        val idFinish = max(bound1, bound2)

        if (repository.qBulkDeleteByChatIdFromIdToId(chatId, idStart, idFinish) == 0) {
            throw MessageNotFoundException("No messages deleted")
        }
    }

}