package com.yourRPG.chatPG.model

import jakarta.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "account")
class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var id: Long? = null

    @Column(unique = true, nullable = false)
    private var name: String? = null

    @NotNull
    private var password: String? = null

    @ManyToMany(mappedBy = "accounts")
    var chats: MutableList<Chat>? = null

    @OneToMany(mappedBy = "account")
    var messages: MutableList<Message?>? = null

    constructor(name: String?) {
        this.name = name
    }

    constructor()

    fun getName(): String? {
        return name
    }

    fun getId(): Long? {
        return id
    }

    fun passwordMatches(password: String?): Boolean {
        return this.password == password
    }

}