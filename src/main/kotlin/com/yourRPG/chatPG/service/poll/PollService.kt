package com.yourRPG.chatPG.service.poll

import com.yourRPG.chatPG.dto.poll.PollDto
import com.yourRPG.chatPG.exception.poll.AlreadyVotedInPollException
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
class PollService(
    /* Services */
    private val runner: PollCommandRunnerService,
    private val chatService: ChatService,

    /* Repositories */
    private val pollRepo: PollRepository
): IConvertible<Poll, PollDto> {

    /* Conversion */
    override fun dto(c: Poll): PollDto =
        PollDto(
            chatService.dto(c = c.getChat() ?: Chat()),
            subject = c.getSubject(),
            quota   = c.getQuota(),
            votes   = c.getVotes().size
        )

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
    fun all(accountId: Long, chatId: Long): List<PollDto> {
        chatService.getByAccountIdAndChatId(accountId, chatId)

        return pollRepo.findAllByChatId(chatId).toListDto()
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
        val chat: Chat = chatService.getByAccountIdAndChatId(accountId, chatId)

        val subject: PollSubject = PollSubject.valueOf(command)

        if (pollRepo.existsByChatIdAndSubject(chatId, subject)) {
            throw PollAlreadyExistsException("Cannot start a new poll because one of the same matter is already active")
        }

        val poll = Poll(chat, subject, (chat.getAmountOfAccounts() + 1) / 2)

        pollRepo.save(poll)

        return poll.toDto()
    }

    /**
     * Adds a vote as [accountId] to the [Poll] active in [Chat] with subject of the [command], identifying a single object,
     *  since [Chat] and [PollSubject] make up a primary key for [Poll].
     *
     * @param accountId
     * @param chatId
     * @param command
     * @return [PollDto] of the [Poll] identified.
     * @throws com.yourRPG.chatPG.exception.account.AccountNotFoundException if [accountId] did not identify an account.
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException if [chatId] did not identify a chat.
     * @throws com.yourRPG.chatPG.exception.chat.AccessToChatUnauthorizedException if the [com.yourRPG.chatPG.model.Account]
     *  identified by [accountId] does not have access to the [Chat] found by [chatId].
     * @throws AlreadyVotedInPollException if the [accountId] is already present on the list of votes of the [Poll].
     */
    fun vote(accountId: Long, chatId: Long, command: String): PollDto {
        val chat: Chat = chatService.getByAccountIdAndChatId(accountId, chatId)

        val subject: PollSubject = PollSubject.valueOf(command)

        val poll: Poll = pollRepo.getReferenceById(CompositePrimaryKey(chat, subject))

        if (poll.getVotes().contains(accountId)) {
            throw AlreadyVotedInPollException("That account has already voted in this poll")
        }

        poll.vote(accountId)
        checkPollVotes(poll);

        return poll.toDto()
    }

    /**
     * Checks if the amount of votes reached the quota. If it did, then the subject's respective command runs and the
     *  poll is deleted. Else the changes made to [poll] are persisted.
     *
     * @param poll
     */
    private fun checkPollVotes(poll: Poll) {
        if (poll.getVotes().size >= poll.getQuota()) {
            runner.run(poll)
            pollRepo.delete(poll)
        } else {
            pollRepo.save(poll)
        }
    }

}