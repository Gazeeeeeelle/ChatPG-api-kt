package com.chatpg.service.chat

import com.chatpg.domain.chat.Chat
import com.chatpg.dto.aimodel.AiModelDto
import com.chatpg.dto.chat.ChatDto
import com.chatpg.exception.ai.models.AiModelNotFoundException
import com.chatpg.exception.chat.ChatNotFoundException
import com.chatpg.logging.LoggingUtils
import com.chatpg.mapper.AiModelMapper
import com.chatpg.mapper.ChatMapper
import com.chatpg.repository.ChatRepository
import com.chatpg.service.ai.providers.AiModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val repository: ChatRepository,
    private val mapper: ChatMapper,
    private val aiModelMapper: AiModelMapper
) {

    private companion object {
        val log = LoggingUtils(this)
    }

    /**
     * Returns a [MutableList] of the [Chat]s the account found by [accountId] has access to.
     *
     * @param accountId
     * @return [MutableList] of [ChatDto]
     */
    fun getChatsByAccountId(accountId: Long): List<Chat> =
        repository.qFindByAccountId(accountId)

    /**
     * The fetching of the objects from the database is delegated to [getChatsByAccountId] and then converted to DTO.
     *
     * @param accountId
     * @return [MutableList] of [ChatDto]
     * @see getChatsByAccountId
     */
    fun getChatsDtoByAccountId(accountId: Long): List<ChatDto> =
        getChatsByAccountId(accountId).map(mapper::toDto)

    /**
     * Returns the [AiModelDto] of the [AiModel] active on the chat identified with [publicId].
     *
     * @param publicId
     * @return [AiModelDto]
     * @throws ChatNotFoundException
     */
    fun getModelDto(publicId: UUID): AiModelDto {
        val model = getByPublicId(publicId).model
        return aiModelMapper.toDto(model)
    }

    /**
     * Returns a [Chat] identified by [publicId].
     *
     * @param publicId
     * @return [Chat]
     * @throws ChatNotFoundException
     */
    fun getByPublicId(publicId: UUID): Chat =
        repository.qFindByPublicId(publicId)
            ?: log.run {
                logAndThrow { ChatNotFoundException("Chat not found with Public ID given") }
            }

    /**
     * Similar to [getByPublicId], though, the chat is identified by its name.
     *
     * @param chatName
     * @return [Chat] found by [chatName].
     * @throws ChatNotFoundException
     */
    fun getByChatName(chatName: String): Chat =
        repository.qFindByName(chatName)
            ?: log.run {
                logAndThrow { ChatNotFoundException("Chat not found with Public ID given") }
            }

    /**
     * Delegates to [getByChatName], then converts to DTO.
     *
     * @param chatName
     * @return [ChatDto] of chat found by name.
     * @see getByChatName
     */
    fun getDtoByChatName(chatName: String): ChatDto =
        mapper.toDto(getByChatName(chatName))

    /**
     * Returns the amount of accounts with access to chat with id [publicId]
     *
     * @param publicId
     * @see ChatRepository.countAccounts
     */
    fun getAmountOfAccounts(publicId: UUID): Int =
        repository.countAccounts(publicId)

    /**
     * Updates the model active on a given chat, identified by [publicId], to the model found via [modelNickname].
     *
     * @param publicId Chat's Public Identifier.
     * @param modelNickname Nickname of the AI Model to be selected.
     * @throws AiModelNotFoundException if [modelNickname] did not correspond to any model supported.
     * @see AiModel.findByNickName
     */
    @Transactional
    fun updateChatModel(publicId: UUID, modelNickname: String) {
        val model = AiModel.findByNickName(modelNickname)
            ?: throw AiModelNotFoundException("AI model not found")

        repository.qUpdateModelByPublicId(publicId, model)
    }

}