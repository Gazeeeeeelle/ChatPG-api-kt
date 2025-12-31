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

    //Services
    @Autowired
    private lateinit var runner: PollCommandRunnerService

    @Autowired
    private lateinit var chatService: ChatService

    //Repositories
    @Autowired
    private lateinit var pollRepo: PollRepository

    //Conversion
    override fun Poll.dto(): PollDto {
        return PollDto(
            chatService.dto(c = this.getChat() ?: Chat()),
            subject = this.getSubject(),
            quota   = this.getQuota() ?: -1,
            votes   = this.getVotes().size
        )
    }

    /**
     * Returns all polls active in the chat
     *
     * @param accountId
     * @param chatId
     *
     * @return Mutable list of poll DTOs
     *
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     */
    fun all(accountId: Long, chatId: Long): MutableList<PollDto> {
        chatService.getByAccountIdAndId(accountId, chatId)

        return pollRepo.findAllByChatId(chatId).dto()
    }

    /**
     * Starts a new poll on a chat.
     * The subject given is used to determine what to do when the poll finishes successfully
     *
     * @param accountId
     * @param chatId
     *
     * @return poll DTO
     *
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * if [chatId] did not identify an existing chat
     * or if the account identified by [accountId] did not have access to it
     *
     * @throws
     */
    fun start(accountId: Long, chatId: Long, command: String): PollDto {
        val chat: Chat = chatService.getByAccountIdAndId(accountId, chatId)

        val subject: PollSubject = PollSubject.valueOf(command)

        if (pollRepo.existsByChatIdAndSubject(chatId, subject)) {
            throw PollAlreadyExistsException("Cannot start a new poll because one of the same matter is already active")
        }

        val poll = Poll(chat, subject, (chat.getAmountOfAccounts() + 1) / 2)

        pollRepo.save(poll)

        return poll.dto()
    }

    /**
     * TODO
     */
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

        return poll.dto()
    }


}