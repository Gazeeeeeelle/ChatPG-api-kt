package com.yourRPG.chatPG.domain

import com.yourRPG.chatPG.service.poll.PollSubject
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.io.Serializable

//Since the IDE does not check if the class is implicitly open because of @Entity decorator, we shall suppress the
// misleading warning.
@Suppress("ProtectedInFinal")

@Entity
@Table(name = "poll")
@IdClass(value = Poll.CompositePrimaryKey::class)
class Poll {

    @Id
    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    @NotNull
    lateinit var chat: Chat
        protected set

    @Id
    @Enumerated(EnumType.STRING)
    var subject: PollSubject = PollSubject.NONE
        protected set

    var quota: Int = 1
        protected set

    var votes: MutableSet<Long> = HashSet()
        protected set


    constructor(chat: Chat, subject: PollSubject, quota: Int) {
        this.chat = chat
        this.subject = subject
        this.quota = quota
    }

    protected constructor()

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

            return !(chat != other.chat || subject != other.subject)
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