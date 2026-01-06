package com.yourRPG.chatPG.model

import com.yourRPG.chatPG.service.poll.PollSubject
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "poll")
@IdClass(value = Poll.CompositePrimaryKey::class)
open class Poll {

    @Id
    @ManyToOne
    var chat: Chat? = null
        protected set

    @Id
    @Enumerated(EnumType.STRING)
    var subject: PollSubject = PollSubject.NONE
        protected set

    var quota: Int = 1
        protected set

    var votes: MutableSet<Long> = HashSet()
        protected set


    constructor(chat: Chat?, subject: PollSubject, quota: Int) {
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

    fun vote(accountId: Long) {
        votes.add(accountId)
    }

}