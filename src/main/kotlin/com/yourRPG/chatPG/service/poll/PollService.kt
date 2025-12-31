package com.yourRPG.chatPG.service.poll

import com.yourRPG.chatPG.dto.poll.PollDto
import com.yourRPG.chatPG.exception.poll.PollAlreadyExistsException
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.model.Poll.CompositePrimaryKey
import com.yourRPG.chatPG.repository.PollRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.chat.ChatService
import com.yourRPG.chatPG.service.poll.commandRunner.PollCommandRunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class PollService: IConvertible<Poll, PollDto> {

    @Autowired
    private lateinit var runner: PollCommandRunnerService

    @Autowired
    private lateinit var chatService: ChatService


    @Autowired
    private lateinit var pollRepo: PollRepository


    override fun convert(c: Poll): PollDto {
        return PollDto(
            chatService.convert(c.getChat() ?: Chat()),
            c.getSubject() ?: PollSubject.NONE,
            c.getQuota() ?: -1,
            c.getVotes().size
        )
    }


    fun all(accountId: Long, chatId: Long): MutableList<PollDto> {
        chatService.getByAccountIdAndId(accountId, chatId)

        return convertList(pollRepo.findAllByChatId(chatId))
    }


    fun start(accountId: Long, chatId: Long, command: String): PollDto {
        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)


        val subject: PollSubject = PollSubject.valueOf(command)

        if (pollRepo.existsByChatIdAndSubject(chatId, subject)) {
            throw PollAlreadyExistsException("Cannot start a new poll because one of the same matter is already active")
        }

        val poll = Poll(chat, subject, (chat.getAmountOfAccounts() + 1) / 2)

        pollRepo.save(poll)

        return convert(poll)
    }

    fun vote(accountId: Long, chatId: Long, command: String): PollDto {
        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        val subject: PollSubject = PollSubject.valueOf(command)

        val poll: Poll = pollRepo.getReferenceById(CompositePrimaryKey(chat, subject))

        poll.vote(accountId)

        if (poll.getVotes().size >= poll.getQuota()!!) {
            runner.run(poll)
            pollRepo.delete(poll)
        } else {
            pollRepo.save(poll)
        }

        return convert(poll)
    }


}