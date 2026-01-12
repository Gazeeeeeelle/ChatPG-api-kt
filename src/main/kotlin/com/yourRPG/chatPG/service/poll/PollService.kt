package com.yourRPG.chatPG.service.poll

import com.yourRPG.chatPG.dto.poll.PollDto
import com.yourRPG.chatPG.exception.poll.AlreadyVotedInPollException
import com.yourRPG.chatPG.model.Chat
import com.yourRPG.chatPG.model.Poll
import com.yourRPG.chatPG.model.Poll.CompositePrimaryKey
import com.yourRPG.chatPG.repository.PollRepository
import com.yourRPG.chatPG.service.IConvertible
import com.yourRPG.chatPG.service.chat.ChatService
import com.yourRPG.chatPG.service.poll.commandRunner.PollCommandRunnerService
import com.yourRPG.chatPG.validator.poll.PollValidators
import org.springframework.stereotype.Service

@Service
class PollService(
    /* Services */
    private val runnerService: PollCommandRunnerService,
    private val chatService: ChatService,

    /* Repositories */
    private val pollRepository: PollRepository,

    /* Validators */
    private val pollValidators: PollValidators
): IConvertible<Poll, PollDto> {

    /* Conversion */
    override fun dtoOf(c: Poll): PollDto =
        PollDto(
            chat = chatService.dtoOf(c = c.chat),
            c.subject,
            c.quota,
            votes = c.votes.size
        )

    /**
     * Returns all polls active in the chat.
     *
     * @param chatId
     *
     * @return Mutable list of poll DTOs.
     *
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     */
    fun all(chatId: Long): List<PollDto> {
        chatService.getByChatId(chatId)

        return pollRepository.findAllByChatId(chatId).toListDto()
    }

    /**
     * Starts a new poll on a chat.
     * The subject given is used to determine what to do when the poll finishes successfully.
     *
     * @param accountId
     * @param chatId
     *
     * @return poll DTO
     *
     * @throws com.yourRPG.chatPG.exception.chat.ChatNotFoundException
     * if [chatId] did not identify an existing chat
     * or if the account identified by [accountId] did not have access to it
     */
    fun start(accountId: Long, chatId: Long, command: String): PollDto {
        
        val chat = chatService.getByChatId(chatId)
        
        val subject = PollSubject.valueOf(command)

        val amountOfAccountsInChat = chatService.getAmountOfAccounts(chatId)
        
        val poll = Poll(chat, subject, quota = (amountOfAccountsInChat + 1) / 2)

        pollValidators.validateStart(accountId, chat, poll)

        pollRepository.save(poll)

        return poll.toDto()
    }

    /**
     * Adds a vote as [accountId] to the [Poll] active in [Chat] with subject of the [command], identifying a single object,
     *  since [Chat] and [PollSubject] make up a primary key for [Poll].
     *
     * @param accountId
     * @param chatId
     * @param command
     * 
     * @return [PollDto] of the [Poll] identified.
     * 
     * @throws AlreadyVotedInPollException if the [accountId] is already present on the list of votes of the [Poll].
     */
    fun vote(accountId: Long, chatId: Long, command: String): PollDto {

        val chat = chatService.getByChatId(chatId)

        val subject = PollSubject.valueOf(command)

        val poll = pollRepository.getReferenceById(CompositePrimaryKey(chat, subject))

        pollValidators.validateVote(accountId, poll)

        poll.vote(accountId)
        checkPollVotes(poll)

        return poll.toDto()
    }

    /**
     * Checks if the amount of votes reached the quota. If it did, then the subject's respective command runs and the
     *  poll is deleted. Else the changes made to [poll] are persisted.
     *
     * @param poll
     */
    private fun checkPollVotes(poll: Poll) {
        if (poll.votes.size >= poll.quota) {
            runnerService.run(poll)
            pollRepository.delete(poll)
        } else {
            pollRepository.save(poll)
        }
    }

}