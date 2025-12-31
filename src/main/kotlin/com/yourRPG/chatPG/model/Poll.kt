package com.yourRPG.chatPG.model

import com.yourRPG.chatPG.exception.poll.AlreadyVotedInPollException
import com.yourRPG.chatPG.service.poll.PollSubject
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.io.Serializable


@Entity
@Table(name = "poll")
@IdClass(value = Poll.CompositePrimaryKey::class)
class Poll {

    @Id
    @ManyToOne
    private var chat: Chat? = null

    @Id
    @Enumerated(EnumType.STRING)
    private var subject: PollSubject = PollSubject.NONE

    private var quota: Int? = null

    private var voteIds: MutableList<Long?> = ArrayList<Long?>()

    constructor(chat: Chat?, subject: PollSubject, quota: Int?) {
        this.chat = chat
        this.subject = subject
        this.quota = quota
    }

    constructor()

    class CompositePrimaryKey : Serializable {
        private var chat: Chat? = null
        private var subject: PollSubject? = null

        constructor(chat: Chat?, subject: PollSubject?) {
            this.chat = chat
            this.subject = subject
        }

        constructor()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CompositePrimaryKey

            if (chat != other.chat) return false
            if (subject != other.subject) return false

            return true
        }

        override fun hashCode(): Int {
            var result = chat?.hashCode() ?: 0
            result = 31 * result + (subject?.hashCode() ?: 0)
            return result
        }


    }

    fun getChat(): Chat? {
        return chat
    }

    fun getSubject(): PollSubject? {
        return subject
    }

    fun getQuota(): Int? {
        return quota
    }

    fun getVotes(): MutableList<Long?> {
        return voteIds
    }

    fun vote(accountId: Long?) {

        //TODO maybe putting this in a validator too should be good
        //FIXME might be better to refactor this into PollService.kt, since it is a contract
        if (voteIds.contains(accountId)) {
            throw AlreadyVotedInPollException("That account has already voted in this poll")
        }

        voteIds.add(accountId)
    }

}