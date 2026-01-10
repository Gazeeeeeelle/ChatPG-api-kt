package com.yourRPG.chatPG.service.chat

import com.yourRPG.chatPG.dto.ai.model.AiModelDto
import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.exception.ai.models.AiModelNotFoundException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.repository.ChatRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.ai.providers.AiModel
import org.springframework.stereotype.Service

@Service
class ChatService(
    /* Services */
    private val aiService: AiService,

    /* Repositories */
    private val repository: ChatRepository,
): IConvertible<Chat, ChatDto> {

    /* Conversion */
    override fun dtoOf(c: Chat): ChatDto = ChatDto(c)

    /**
     * Returns a [MutableList] of the [Chat]s the account found by [accountId] has access to.
     *
     * @param accountId
     * @return [MutableList] of [ChatDto]
     */
    fun getChatsByAccountId(accountId: Long): List<Chat> {
        return repository.qFindByAccountId(accountId)
    }

    /**
     * The fetching of the objects from the database is delegated to [getChatsByAccountId] and then converted to DTO.
     *
     * @param accountId
     * @return [MutableList] of [ChatDto]
     * @see getChatsByAccountId
     */
    fun getChatsDtoByAccountId(accountId: Long): List<ChatDto> {
        return getChatsByAccountId(accountId).toListDto()
    }

    /**
     * Returns the [AiModelDto] of the [AiModel] active on the chat identified with [chatId].
     *
     * @param chatId
     * @return [AiModelDto]
     * @throws ChatNotFoundException
     */
    fun getModelDto(chatId: Long): AiModelDto {
        val chat: Chat = getByChatId(chatId)

        return aiService.dtoOf(chat.model)
    }

    /**
     * Returns a [Chat] identified by [chatId].
     *
     * @param chatId
     * @return [Chat]
     * @throws ChatNotFoundException
     */
    fun getByChatId(chatId: Long): Chat {
        return repository.findById(chatId).orElse(null)
            ?: throw ChatNotFoundException("Chat with id $chatId not found")
    }

    /**
     * Similar to [getByChatId], though, the chat is identified by its name.
     *
     * @param chatName
     * @return [Chat] found by [chatName].
     * @throws ChatNotFoundException
     */
    fun getByChatName(chatName: String): Chat {
        return repository.qFindByName(chatName)
            ?: throw ChatNotFoundException("Chat '$chatName' not found")
    }

    /**
     * Changes the model active on a given chat, identified by [chatId], to the model found via [modelNickname].
     *
     * @param chatId
     * @param modelNickname
     * @throws AiModelNotFoundException if [AiModel.findByNickName] returned null, meaning the [modelNickname] did not correspond to any model supported.
     * @see getByChatId
     * @see AiModel.findByNickName
     */
    fun chooseModelForChat(chatId: Long, modelNickname: String) {
        val chat = getByChatId(chatId)

        val model = AiModel.findByNickName(modelNickname)
            ?: throw AiModelNotFoundException("AI model not found")

        chat.model = model

        repository.save(chat)
    }

    /**
     * Delegates to [getByChatName], then converts to DTO.
     *
     * @param chatName
     */
    fun getDtoByAccountIdAndChatId(chatName: String): ChatDto? {
        return getByChatName(chatName).toDto()
    }

}