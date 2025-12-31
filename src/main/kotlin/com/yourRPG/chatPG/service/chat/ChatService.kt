package com.yourRPG.chatPG.service.chat

import com.yourRPG.chatPG.dto.chat.AiModelDto
import com.yourRPG.chatPG.dto.chat.ChatDto
import com.yourRPG.chatPG.exception.ai.models.AiModelNotFoundException
import com.yourRPG.chatPG.exception.chat.ChatNotFoundException
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.repository.ChatRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.ICanNotBeFound
import com.yourRPG.chatPG.service.ai.providers.AiModel
import com.yourRPG.chatPG.validator.PresenceValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ChatService: IConvertible<Chat, ChatDto>, ICanNotBeFound {

    @Autowired
    private lateinit var accountService: AccountService;

    @Autowired
    private lateinit var aiService: AiService;


    @Autowired
    private lateinit var repository: ChatRepository;


    //Validators TODO
    var presentValidator: PresenceValidator<Chat> = PresenceValidator(ChatNotFoundException("Chat not found"))


    override fun convert(c: Chat): ChatDto {
        return ChatDto(c)
    }

    override fun getNotFoundException(): RuntimeException {
        return ChatNotFoundException("Chat could not be found")
    }


    fun getByAccountId(id: Long): MutableList<ChatDto> {
        return convertList(c = repository.qFindByAccountId(id))
    }

    fun getChats(accountId: Long): MutableList<ChatDto> {
        accountService.getPureById(accountId) //just to check if account exists TODO: validation should be the one taking care of this

        return getByAccountId(accountId)
    }


    fun getChosenModel(accountId: Long, chatId: Long): AiModelDto {
        val chat: Chat = getByAccountIdAndId(accountId, chatId)

        return aiService.convert(chat.getModel())
    }


    fun getByAccountIdAndId(accountId: Long, chatId: Long): Chat {
        val chat: Chat? = repository.qFindByAccountIdAndId(accountId, chatId)
        presentValidator.validate(chat)

        return chat!! /* FIXME DAGGER_1 */
    }

    fun getDtoByAccountIdAndChatName(accountId: Long, chatName: String): ChatDto {
        val chat: Chat? = repository.qFindByAccountIdAndChatName(accountId, chatName)
        presentValidator.validate(chat)

        return convert(chat!!) /* FIXME DAGGER_1 */
    }

    fun chooseModelForChat(accountId: Long, chatId: Long, modelNickname: String) {
        val chat: Chat = getByAccountIdAndId(accountId, chatId)

        val model: AiModel? = AiModel.findByNickName(modelNickname)

        //TODO Make a validator for this
        if (model === null) {
            throw AiModelNotFoundException("Was not able to find AI model.")
        }

        chat.setModel(model)

        repository.save(chat)
    }

    /*
    FIXME: DAGGER_1
    '!!' might be able to be removed after implementation of the validator, since it throws a child class of IllegalStateException.
    */

}