package com.yourRPG.chatPG.model

import jakarta.persistence.*


@Entity
@Table(name = "message")
class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var id: Long? = null

    @Column(nullable = false, length = 20000)
    private var content: String? = null

    @ManyToOne
    private var chat: Chat? = null

    @Column(nullable = false)
    private var bot: Boolean = false

    @ManyToOne(fetch = FetchType.LAZY)
    private var account: Account? = null

    constructor(content: String?, chat: Chat?, bot: Boolean, account: Account?) {
        this.content = content
        this.chat = chat
        this.bot = bot
        this.account = account
    }

    constructor()

    fun getAccount(): Account? {
        return account
    }

    fun getContent(): String? {
        return content
    }

    fun getId(): Long? {
        return id
    }

    fun getChat(): Chat? {
        return chat
    }

    fun isBot(): Boolean {
        return bot
    }

}