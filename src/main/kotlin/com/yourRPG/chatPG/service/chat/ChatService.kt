package com.yourRPG.chatPG.service.chat

import com.yourRPG.chatPG.dto.ai.model.AiModelDto
import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.exception.ai.models.AiModelNotFoundException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.repository.ChatRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.validator.PresenceValidator
import com.yourRPG.chatPG.validator.chat.AccountHasAccessToChatValidator
import org.springframework.stereotype.Service

@Service
class ChatService(
    /* Services */
    private val accountService: AccountService,
    private val aiService: AiService,

    /* Repositories */
    private val repository: ChatRepository,

    /* Validators */
    private val accessValidator: AccountHasAccessToChatValidator

): IConvertible<Chat, ChatDto> {

    /* Validators */
    private val presenceValidator =
        PresenceValidator<Chat>(exception = ChatNotFoundException("Chat not found"))
    private val aiModelPresenceValidator =
        PresenceValidator<AiModel>(exception = AiModelNotFoundException("AI model not found"))

    /* Conversion */
    override fun dto(c: Chat): ChatDto = ChatDto(c)

    /**
     * Returns a [MutableList] of the [Chat]s the account found by [accountId] has access to.
     *
     * @param accountId
     * @return [MutableList] of [ChatDto]
     */
    fun getChatsByAccountId(accountId: Long): List<Chat> {
        accountService.getById(accountId)

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
     * Returns the [AiModelDto] of the [AiModel] active on the chat, specified by an [accountId] that identifies the account
     *  requesting and checks if it has access to the chat identified by [chatId].
     *
     * @param accountId
     * @param chatId
     * @return [AiModelDto]
     * @throws ChatNotFoundException
     */
    fun getModelDto(accountId: Long, chatId: Long): AiModelDto {
        val chat: Chat = getByAccountIdAndChatId(accountId, chatId)

        return aiService.dto(chat.getModel())
    }

    /**
     * Returns a [Chat] based on whether the given [accountId] identifies an account that has access to such chat found
     *  identified by [chatId].
     *
     * @param accountId
     * @param chatId
     * @return [Chat]
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     * @see AccountHasAccessToChatValidator.validate
     */
    fun getByAccountIdAndChatId(accountId: Long, chatId: Long): Chat {
        accessValidator.validate(Pair(accountId, chatId))

        return repository.qFindByAccountIdAndId(accountId, chatId)
            ?: throw ChatNotFoundException("Chat with id $accountId not found")
    }

    /**
     * Similar to [getByAccountIdAndChatId], though, the chat is identified by its name.
     *
     * @param accountId
     * @param chatName
     * @return [Chat]
     * @throws ChatNotFoundException
     * @see getByAccountIdAndChatId
     * @see PresenceValidator.validate
     */
    fun getByAccountIdAndChatName(accountId: Long, chatName: String): Chat {
        val nullableChat: Chat? = repository.qFindByAccountIdAndChatName(accountId, chatName)

        return presenceValidator.validate(t = nullableChat)
    }

    /**
     * The fetching of the object is delegated to [getByAccountIdAndChatName] and then converted to DTO.
     *
     * @param accountId
     * @param chatName
     * @return [ChatDto]
     * @throws ChatNotFoundException
     */
    fun getDtoByAccountIdAndChatName(accountId: Long, chatName: String): ChatDto {
        return getByAccountIdAndChatName(accountId, chatName).toDto()
    }

    /**
     * Changes the model active on a given chat, identified by [accountId] and [chatId], to the model found via [modelNickname].
     *
     * @param accountId
     * @param chatId
     * @param modelNickname
     * @throws AiModelNotFoundException if [AiModel.findByNickName] returned null, meaning the [modelNickname] did not correspond to any model supported.
     * @see getByAccountIdAndChatId
     * @see AiModel.findByNickName
     */
    fun chooseModelForChat(accountId: Long, chatId: Long, modelNickname: String) {
        val chat: Chat = getByAccountIdAndChatId(accountId, chatId)

        val model :AiModel = aiModelPresenceValidator.validate(
            t = AiModel.findByNickName(modelNickname)
        )

        chat.setModel(model)

        repository.save(chat)
    }

    /**
     * This method is wrapper for [AccountHasAccessToChatValidator.validate]. Its main purpose is to enable the
     *  validation of pair accountId and chatId without the need of using a method like [getByAccountIdAndChatId], which
     *  fetches objects from the database. The existence of this method discards the need of wiring a
     *  [AccountHasAccessToChatValidator] on many services.
     *
     * @param [pair] of (accountId, chatId)
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException
     */
    fun validateAccess(pair: Pair<Long, Long>) {
        accessValidator.validate(pair)
    }

}