package com.yourRPG.chatPG.model

import jakarta.persistence.*


@Entity
@Table(name = "message")
open class Message {

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

    constructor()

}