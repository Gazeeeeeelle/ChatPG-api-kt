package com.chatpg.domain.message

import com.chatpg.domain.account.Account
import com.chatpg.domain.chat.Chat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

//Since the IDE does not check if the class is implicitly open because of @Entity decorator, we shall suppress the
// misleading warning.
@Suppress("ProtectedInFinal")

@Entity
@Table(name = "message")
class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null
        protected set

    @Column(nullable = false, length = 20000)
    var content: String = ""
        protected set

    @ManyToOne
    var chat: Chat? = null
        protected set

    @Column(nullable = false)
    var bot: Boolean = false
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    var account: Account? = null
        protected set

    constructor(account: Account?, chat: Chat, content: String, bot: Boolean) {
        this.content = content
        this.chat = chat
        this.bot = bot
        this.account = account
    }

    protected constructor()

}