package com.yourRPG.chatPG.service.message

import com.yourRPG.chatPG.dto.message.MessageDto
import com.yourRPG.chatPG.exception.ai.NullAiResponse
import com.yourRPG.chatPG.model.Account
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Message
import com.yourRPG.chatPG.repository.MessageRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.account.AccountService
import com.yourRPG.chatPG.service.ai.AiService
import com.yourRPG.chatPG.service.chat.ChatService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class MessageService: IConvertible<Message, MessageDto> {

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var chatService: ChatService

    @Autowired
    private lateinit var chatGPService: ChatpgService

    @Autowired
    private lateinit var aiService: AiService


    @Autowired
    private lateinit var repository: MessageRepository


    override fun convert(c: Message): MessageDto {
        return if (c.isBot())
            MessageDto(
                c.getId() ?: -1,
                c.getContent() ?: "!!! NO_CONTENT !!!",
                c.isBot(),
                null
            )
        else MessageDto(c)
    }


    fun getMessagesInChatByChatId(id: Long?): MutableList<MessageDto> {
        return convertList(repository.qFindAllByChatId(id))
    }

    fun getMessagesInChat(accountId: Long, chatId: Long): MutableList<MessageDto> {
        //check if account has access to chat
        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        return getMessagesInChatByChatId(chat.getId())
    }


    fun sendMessage(accountId: Long, chatId: Long, message: String): MessageDto {
        val account = accountService.getPureById(accountId)

        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        return createMessage(account, chat, message, false)
    }

    private fun createMessage(account: Account?, chat: Chat, message: String, isBot: Boolean): MessageDto {
        if (message.isBlank()) {

        }

        val msg = Message(message, chat, isBot, account)

        repository.save(msg)

        return convert(msg)
    }

    fun generateResponse(accountId: Long, chatId: Long): MessageDto {
        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        return createAIMessage(chat)
    }

    fun createAIMessage(chat: Chat): MessageDto {
        val previousMessages: MutableList<Message> = repository.qFindAllMessagesFromChat(chat)

        val memoryPrompt: String = chatGPService.treatMemoryForPrompt(previousMessages)

        val aiResponse: String? = aiService.askAI(chat.getModel(), memoryPrompt)
        if (aiResponse === null) {
            throw NullAiResponse("Response was null")
        }

        return createMessage(null, chat, aiResponse, true)
    }


    @Transactional
    fun deleteMessage(accountId: Long, chatId: Long, id: Long): Int {
        chatService.getByAccountIdAndId(accountId, chatId)

        return repository.qDeleteByChatIdAndId(chatId, id)
    }

    @Transactional
    fun bulkDeleteMessages(accountId: Long, chatId: Long, idStart: Long, idFinish: Long): Int {
        require(idStart < idFinish) { "idFinish cannot be greater or equal to idStart" }

        chatService.getByAccountIdAndId(accountId, chatId)

        return repository.qBulkDeleteByChatIdFromIdToId(chatId, idStart, idFinish)
    }


}