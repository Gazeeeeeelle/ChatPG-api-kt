package com.yourRPG.chatPG.service.chat

import com.yourRPG.chatPG.domain.chat.Chat
import com.yourRPG.chatPG.dto.aimodel.AiModelDto
import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.exception.ai.models.AiModelNotFoundException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.mapper.AiModelMapper
import com.yourRPG.chatPG.mapper.ChatMapper
import com.yourRPG.chatPG.repository.ChatRepository
import com.yourRPG.chatPG.service.ai.providers.AiModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val repository: ChatRepository,
    private val mapper: ChatMapper,
    private val aiModelMapper: AiModelMapper
) {

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
    fun getModelDto(publicId: UUID): AiModelDto =
        aiModelMapper.toDto(getByPublicId(publicId).model)

    /**
     * Returns a [Chat] identified by [publicId].
     *
     * @param publicId
     * @return [Chat]
     * @throws ChatNotFoundException
     */
    fun getByPublicId(publicId: UUID): Chat =
        repository.qFindByPublicId(publicId)
            ?: throw ChatNotFoundException("Chat not found with public id given")

    /**
     * Similar to [getByPublicId], though, the chat is identified by its name.
     *
     * @param chatName
     * @return [Chat] found by [chatName].
     * @throws ChatNotFoundException
     */
    fun getByChatName(chatName: String): Chat =
        repository.qFindByName(chatName)
            ?: throw ChatNotFoundException("Chat '$chatName' not found")

    /**
     * Updates the model active on a given chat, identified by [publicId], to the model found via [modelNickname].
     *
     * @param publicId
     * @param modelNickname
     * @throws AiModelNotFoundException if [AiModel.findByNickName] returned null, meaning the [modelNickname] did not correspond to any model supported.
     * @see getByPublicId
     * @see AiModel.findByNickName
     */
    @Transactional
    fun updateChatModel(publicId: UUID, modelNickname: String) {
        val chat = getByPublicId(publicId)

        chat.model = AiModel.findByNickName(modelNickname)
            ?: throw AiModelNotFoundException("AI model not found")

        repository.save(chat)
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

}