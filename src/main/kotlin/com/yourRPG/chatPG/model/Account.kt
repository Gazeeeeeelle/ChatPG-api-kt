package com.yourRPG.chatPG.model

import jakarta.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "account")
open class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null
        protected set

    @Column(unique = true, nullable = false)
    var name: String? = null
        protected set

    @NotNull
    var password: String? = null
        protected set

    @ManyToMany(mappedBy = "accounts")
    var chats: MutableList<Chat>? = null
        protected set

    @OneToMany(mappedBy = "account")
    var messages: MutableList<Message?>? = null
        protected set

    constructor(name: String?) {
        this.name = name
    }

    constructor()

    fun passwordMatches(password: String?): Boolean {
        return this.password == password
    }

}