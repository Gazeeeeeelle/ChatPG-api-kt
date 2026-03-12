package com.chatpg.service.poll

import com.chatpg.domain.chat.Chat
import com.chatpg.domain.poll.Poll
import com.chatpg.domain.poll.Poll.CompositePrimaryKey
import com.chatpg.dto.poll.PollDto
import com.chatpg.exception.http.sc4xx.BadRequestException
import com.chatpg.exception.poll.AlreadyVotedInPollException
import com.chatpg.exception.poll.PollNotFoundException
import com.chatpg.mapper.PollMapper
import com.chatpg.repository.PollRepository
import com.chatpg.service.chat.ChatService
import com.chatpg.service.poll.commandRunner.PollCommandRunnerService
import com.chatpg.validator.poll.PollValidators
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class PollService(
    private val runnerService: PollCommandRunnerService,
    private val chatService: ChatService,

    private val pollRepository: PollRepository,

    private val pollValidators: PollValidators,

    private val mapper: PollMapper
) {

    /**
     * Returns poll of subject [subject] in chat [chat].
     *
     * @param chat where to look for such poll.
     * @param subject specific matter the poll must address.
     * @return [Poll] found.
     * @throws PollNotFoundException if there is no such poll with that subject in that chat.
     */
    fun getById(chat: Chat, subject: PollSubject): Poll =
        pollRepository.findById(CompositePrimaryKey(chat, subject)).getOrElse {
            throw PollNotFoundException("No poll with subject \"${subject.name}\" in chat given")
        }

    /**
     * Returns all polls active in the chat.
     *
     * @param publicChatId
     *
     * @return Mutable list of poll DTOs.
     *
     * @throws com.chatpg.exception.chat.ChatNotFoundException
     */
    fun all(publicChatId: UUID): List<PollDto> =
        pollRepository.qFindAllByPublicChatId(publicChatId).map(mapper::toDto)

    /**
     * Starts a new poll on a chat.
     * The subject given is used to determine what to do when the poll finishes successfully.
     *
     * @param accountId
     * @param publicChatId
     *
     * @return poll DTO
     *
     * @throws com.chatpg.exception.chat.ChatNotFoundException
     * if [publicChatId] did not identify an existing chat
     * or if the account identified by [accountId] did not have access to it
     */
    fun start(accountId: Long, publicChatId: UUID, command: String): PollDto {
        val chat = chatService.getByPublicId(publicChatId)

        val subject = try {
            PollSubject.valueOf(command)
        } catch (_: IllegalArgumentException) {
           throw BadRequestException("Invalid command: $command")
        }

        val amountOfAccountsInChat = chatService.getAmountOfAccounts(publicChatId)
        
        val poll = Poll(chat, subject, quota = (amountOfAccountsInChat + 1) / 2)

        pollValidators.validateStart(accountId, chat, poll)

        return mapper.toDto(pollRepository.save(poll))
    }

    /**
     * Adds a vote as [accountId] to the [Poll] active in [Chat] with subject of the [command], identifying a single object,
     *  since [Chat] and [PollSubject] make up a primary key for [Poll].
     *
     * @param accountId account identifier
     * @param publicChatId chat identifier
     * @param command
     * 
     * @return [PollDto] of the [Poll] identified.
     * 
     * @throws AlreadyVotedInPollException if the [accountId] is already present on the list of votes of the [Poll].
     */
    fun vote(accountId: Long, publicChatId: UUID, command: String): PollDto {

        val chat = chatService.getByPublicId(publicChatId)

        val subject = runCatching { PollSubject.valueOf(command) }
            .getOrElse { throw BadRequestException("No such command: $command") }

        val poll = getById(chat, subject)

        pollValidators.validateVote(accountId, poll)

        poll.vote(accountId)
        checkPollVotes(poll)

        return mapper.toDto(poll)
    }

    /**
     * Checks if the amount of votes reached the quota. If it did, then the subject's respective command runs and the
     *  poll is deleted. Else the changes made to [poll] are persisted.
     *
     * @param poll
     */
    @Transactional
    private fun checkPollVotes(poll: Poll) {
        if (poll.votes.size >= poll.quota) {
            runnerService.run(poll)
            pollRepository.delete(poll)
        } else {
            pollRepository.save(poll)
        }
    }

}