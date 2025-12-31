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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ChatService: IConvertible<Chat, ChatDto> {

    //Services
    @Autowired
    private lateinit var accountService: AccountService;

    @Autowired
    private lateinit var aiService: AiService;

    //Repositories
    @Autowired
    private lateinit var repository: ChatRepository;

    //Validators
    private val presenceValidator = PresenceValidator<Chat>(ChatNotFoundException("Chat not found"))

    private val aiModelPresenceValidator = PresenceValidator<AiModel>(AiModelNotFoundException("AI model not found"))

    //Conversion
    override fun Chat.dto(): ChatDto {
        return ChatDto(this)
    }

    /**
     * TODO
     */
    fun getByAccountId(id: Long): MutableList<ChatDto> {
        return repository.qFindByAccountId(id).dto()
    }

    /**
     * TODO
     */
    fun getChats(accountId: Long): MutableList<ChatDto> {
        accountService.getById(accountId)

        return getByAccountId(accountId)
    }

    /**
     * TODO
     */
    fun getChosenModel(accountId: Long, chatId: Long): AiModelDto {
        val chat: Chat = getByAccountIdAndId(accountId, chatId)

        return aiService.dto(chat.getModel())
    }

    /**
     * TODO
     */
    fun getByAccountIdAndId(accountId: Long, chatId: Long): Chat {
        val chat: Chat = presenceValidator.validate(
            t = repository.qFindByAccountIdAndId(accountId, chatId)
        )

        return chat
    }

    /**
     * TODO
     */
    fun getDtoByAccountIdAndChatName(accountId: Long, chatName: String): ChatDto {

        accountService.getById(accountId)

        val chat: Chat = presenceValidator.validate(
            t = repository.qFindByAccountIdAndChatName(accountId, chatName)
        )

        return chat.dto()
    }

    /**
     * TODO
     */
    fun chooseModelForChat(accountId: Long, chatId: Long, modelNickname: String) {
        val chat: Chat = getByAccountIdAndId(accountId, chatId)

        val model :AiModel = aiModelPresenceValidator.validate(
            t = AiModel.findByNickName(modelNickname)
        )

        chat.setModel(model)

        repository.save(chat)
    }

}