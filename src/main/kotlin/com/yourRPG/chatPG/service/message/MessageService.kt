package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.exception.ai.NullAiResponse
import com.yourRPG.chatPG.exception.message.BlankMessageContentException
import com.yourRPG.chatPG.exception.message.MessageContentNotFoundException
import com.yourRPG.chatPG.exception.message.MessageNotFoundException
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Message
import com.yourRPG.chatPG.repository.MessageRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.chat.ChatService
import com.yourRPG.chatPG.validator.IValidatable
import com.yourRPG.chatPG.validator.PresenceValidator
import com.yourRPG.chatPG.validator.Validator
import com.yourRPG.chatPG.validator.message.MessageContentValidator
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class MessageService: IConvertible<Message, MessageDto> {

    //Services
    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var chatService: ChatService

    @Autowired
    private lateinit var chatGPService: ChatpgService

    @Autowired
    private lateinit var aiService: AiService

    //Repositories
    @Autowired
    private lateinit var repository: MessageRepository

    //Validators
    private val contentValidator = MessageContentValidator()

    //Conversion
    override fun Message.dto(): MessageDto {
        return if (this.isBot())
            MessageDto(
                id      = this.getId() ?: -1,
                content = this.getContent(),
                bot     = this.isBot(),
                account = null
            )
        else MessageDto(this)
    }

    /**
     * TODO
     */
    fun getMessagesInChatByChatId(id: Long?): MutableList<MessageDto> {
        return repository.qFindAllByChatId(id).dto()
    }

    /**
     * TODO
     */
    fun getMessagesInChat(accountId: Long, chatId: Long): MutableList<MessageDto> {
        //check if account has access to chat
        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        return getMessagesInChatByChatId(chat.getId())
    }


    /**
     * TODO
     */
    fun sendMessage(accountId: Long, chatId: Long, message: String): MessageDto {
        val account = accountService.getById(accountId)

        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        return createMessage(account, chat, message, false)
    }

    /**
     * TODO
     */
    private fun createMessage(account: Account?, chat: Chat, message: String, isBot: Boolean): MessageDto {
        contentValidator.validate(t = message)

        val msg = Message(content = message, chat, isBot, account)

        repository.save(msg)

        return msg.dto()
    }

    /**
     * TODO
     */
    fun generateResponse(accountId: Long, chatId: Long): MessageDto {
        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        return createAIMessage(chat)
    }

    /**
     * TODO
     */
    fun createAIMessage(chat: Chat): MessageDto {
        val previousMessages: MutableList<Message> = repository.qFindAllMessagesFromChat(chat)

        val memoryPrompt: String = chatGPService.treatMemoryForPrompt(previousMessages)

        val aiResponse: String = aiService.askAI(chat.getModel(), memoryPrompt)
            ?: throw NullAiResponse("Response was null")

        return createMessage(account = null, chat, message = aiResponse, true)
    }

    /**
     * TODO
     */
    @Transactional
    fun deleteMessage(accountId: Long, chatId: Long, id: Long) {
        chatService.getByAccountIdAndId(accountId, chatId)

        if (repository.qDeleteByChatIdAndId(chatId, id) == 0) {
            throw MessageNotFoundException("No messages deleted")
        }
    }

    /**
     * TODO
     */
    @Transactional
    fun bulkDeleteMessages(accountId: Long, chatId: Long, idStart: Long, idFinish: Long) {
        require(idStart < idFinish) { "idFinish cannot be greater or equal to idStart" }

        chatService.getByAccountIdAndId(accountId, chatId)

        if (repository.qBulkDeleteByChatIdFromIdToId(chatId, idStart, idFinish) == 0) {
            throw MessageNotFoundException("No messages deleted")
        }
    }

}