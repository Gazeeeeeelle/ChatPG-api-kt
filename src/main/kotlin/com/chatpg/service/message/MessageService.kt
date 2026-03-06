package com.chatpg.service.message

import com.chatpg.domain.account.Account
import com.chatpg.domain.chat.Chat
import com.chatpg.domain.message.Message
import com.chatpg.dto.message.MessageDto
import com.chatpg.exception.ai.NullAiResponse
import com.chatpg.exception.message.MessageNotFoundException
import com.chatpg.mapper.MessageMapper
import com.chatpg.repository.MessageRepository
import com.chatpg.service.account.AccountService
import com.chatpg.service.ai.AiService
import com.chatpg.service.chat.ChatService
import com.chatpg.validator.message.MessageContentValidator
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
            -1L -> repository.qFindOldByChatIdAndReference(publicChatId, reference = Long.MAX_VALUE)
            else -> repository.qFindOldByChatIdAndReference(publicChatId, referenceId)
        }.map(mapper::toDto)
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

        return repository.qFindNewByChatIdAndReference(publicChatId, referenceId)
            .map(mapper::toDto)
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
        val message =
            createMessage(
                accountService.getById(accountId),
                chatService.getByPublicId(publicChatId),
                content
            )
        return mapper.toDto(message)
    }

    /**
     * Creates and persists a message as a Bot.
     *
     * @param chat chat where the message is gonna be assigned to under creation.
     * @param content message content.
     * @see MessageContentValidator.validate
     */
    @Transactional
    private fun createBotMessage(chat: Chat, content: String): Message {
        contentValidator.validate(t = content)

        val message = Message(null, chat, content, bot = true)
        return repository.save(message)
    }

    /**
     * Creates and persists a message as the Account given.
     *
     * @param account who sent the message (can be null if a bot).
     * @param chat chat where the message is gonna be assigned to under creation.
     * @param content message content.
     * @see MessageContentValidator.validate
     */
    @Transactional
    private fun createMessage(account: Account, chat: Chat, content: String): Message {
        contentValidator.validate(t = content)

        val message = Message(account, chat, content, bot = false)
        return repository.save(message)
    }

    /**
     * Generates an AI message on a chat identified by [publicChatId].
     *
     * @param publicChatId chat identifier
     * @see ChatService.getByPublicId
     * @see createAIMessage
     */
    fun generateResponse(publicChatId: UUID): MessageDto =
        createAIMessage(chatService.getByPublicId(publicChatId))

    /**
     * Fetches previous messages present in the chat ([MessageRepository.qFindAllMessagesFromChat]), then treats them
     *  ([ChatpgService.treatMemoryForPrompt]) to use as context for the generation of a new message. Requests the
     *  generation of a message with [AiService.askAi].
     *
     * @param chat
     * @return [MessageDto] of message created
     * @throws NullAiResponse if response from the request of text generation was null.
     * @throws com.chatpg.exception.ai.models.UnavailableAiException if there was a problem with the response
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

        val message = createBotMessage(chat, content)
        return mapper.toDto(message)
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
        repository.qDeleteByChatIdAndId(publicChatId, messageId)
            .takeUnless { it == 0 }
            ?: throw MessageNotFoundException("Message with id $messageId not found")

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