package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.domain.chat.Chat
import com.yourRPG.chatPG.domain.message.Message
import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.exception.ai.NullAiResponse
import com.yourRPG.chatPG.exception.message.MessageNotFoundException
import com.yourRPG.chatPG.mapper.MessageMapper
import com.yourRPG.chatPG.repository.MessageRepository
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.chat.ChatService
import com.yourRPG.chatPG.validator.message.MessageContentValidator
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

@Service
class MessageService(
    private val accountService: AccountService,
    private val chatService: ChatService,
    private val chatPGService: ChatpgService,
    private val aiService: AiService,

    private val repository: MessageRepository,

    private val contentValidator: MessageContentValidator,

    private val mapper: MessageMapper
) {

    /**
     * Fetches 20 messages in the [Chat] identified with and [publicChatId]. The messages are fetched by selecting
     *  the ids less than the [referenceId], and therefore, older messages than the reference.
     * If the [referenceId] equals -1, then it fetches using a [Long.MAX_VALUE], which makes
     *  [MessageRepository.qFindOldByChatIdAndReference] provide the last 20 messages of the chat, since they just need
     *  to have lesser ids than the largest possible.
     *
     * @param publicChatId identifier of chat
     * @param referenceId reference for fetching
     * @return [List] of [MessageDto]s fetched
     * @see MessageRepository.qFindOldByChatIdAndReference
     */
    fun getOldMessagesInChat(publicChatId: UUID, referenceId: Long): List<MessageDto> {
        require(referenceId >= -1L) { "Invalid reference ID: $referenceId" }

        return when (referenceId) {
            -1L -> repository.qFindOldByChatIdAndReference(publicChatId, reference = Long.MAX_VALUE).map(mapper::toDto)
            else -> repository.qFindOldByChatIdAndReference(publicChatId, referenceId).map(mapper::toDto)
        }
    }
    /**
     * Fetches 20 messages in the [Chat] identified with [publicChatId]. The messages are fetched by selecting
     *  the ids greater than the [referenceId], and therefore, newer messages than the reference.
     *
     * @param publicChatId identifier of chat.
     * @param referenceId reference for fetching.
     * @see MessageRepository.qFindNewByChatIdAndReference
     */
    fun getNewMessagesInChat(publicChatId: UUID, referenceId: Long): List<MessageDto> {
        require(referenceId >= -1L) { "Invalid reference ID: $referenceId" }

        return repository.qFindNewByChatIdAndReference(publicChatId, referenceId).map(mapper::toDto)
    }

    /**
     * Fetches a message with id [messageId] in the [Chat] identified with [publicChatId].
     *
     * @return [Message] found
     * @throws MessageNotFoundException if no message with id [messageId] was found in the chat with id [publicChatId]
     * @see MessageRepository.findByPublicChatIdAndId
     */
    fun getByChatIdAndId(publicChatId: UUID, messageId: Long): Message =
        repository.findByPublicChatIdAndId(publicChatId, messageId)
            ?: throw MessageNotFoundException("Unable to find message with id: $messageId")

    /**
     * Delegates to [getByChatIdAndId], then converts to DTO.
     *
     * @param publicChatId
     * @param messageId
     */
    fun getDtoByChatIdAndId(publicChatId: UUID, messageId: Long): MessageDto =
        mapper.toDto(getByChatIdAndId(publicChatId, messageId))

    /**
     * Creates a message as the account identified by [accountId] in the chat identified by [publicChatId] with content [content]
     *
     * @param accountId account identifier.
     * @param publicChatId chat identifier.
     * @param content content of the message.
     * @return [MessageDto] of the message created.
     * @see AccountService.getById
     * @see ChatService.getByPublicId
     * @see createMessage
     */
    fun sendMessage(accountId: Long, publicChatId: UUID, content: String): MessageDto {
        val message = createMessage(
            accountService.getById(accountId),
            chatService.getByPublicId(publicChatId),
            content,
            isBot = false
        )
        return mapper.toDto(message)
    }

    /**
     * Validates content of message, constructs the [Message], then persists it.
     *
     * @param account who sent the message (can be null if a bot).
     * @param chat chat where the message is gonna be assigned to under creation.
     * @param content message content.
     * @param isBot whether the message was sent by a bot or not
     * @see MessageContentValidator.validate
     */
    private fun createMessage(account: Account?, chat: Chat, content: String, isBot: Boolean): Message {
        contentValidator.validate(t = content)

        val message = Message(account, chat, content, isBot)
        return repository.save(message)
    }

    /**
     * Generates an AI message on a chat identified by [publicChatId].
     *
     * @param publicChatId chat identifier
     * @see ChatService.getByPublicId
     * @see createAIMessage
     */
    fun generateResponse(publicChatId: UUID): MessageDto = createAIMessage(chatService.getByPublicId(publicChatId))

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

        return mapper.toDto(createMessage(
            account = null,
            chat,
            content,
            isBot = true
        ))
    }

    /**
     * Deletes a single message in the chat identified by [publicChatId]. The message deleted is identified by its [messageId].
     *
     * @param publicChatId chat identifier
     * @param messageId message identifier
     * @throws MessageNotFoundException if no messages where deleted
     */
    @Transactional
    fun deleteMessage(publicChatId: UUID, messageId: Long) =
        repository.qDeleteByChatIdAndId(publicChatId, messageId).let {
            if (it == 0) throw MessageNotFoundException("Message with id $messageId not found")
        }

    /**
     * Deletes a multiple messages in the chat identified by [publicChatId]. The messages deleted are the ones within the
     *  inclusive range of ids with bounds [bound1] and [bound2]. To enable any order of upper bound and lower bound,
     *  there is a check to pass the idStart and idFinish as the smallest and greater between the bounds respectively.
     *
     * @param publicChatId chat identifier
     * @param bound1 one of the range's bounds
     * @param bound2 another of the range's bounds
     * @throws MessageNotFoundException if no messages where deleted
     */
    @Transactional
    fun bulkDeleteMessages(publicChatId: UUID, bound1: Long, bound2: Long) {
        val idStart  = min(a = bound1, b = bound2)
        val idFinish = max(a = bound1, b = bound2)

        val amountDeleted = repository.qBulkDeleteByChatIdFromIdToId(publicChatId, idStart, idFinish)

        if (amountDeleted == 0) throw MessageNotFoundException("No messages deleted")
    }

}