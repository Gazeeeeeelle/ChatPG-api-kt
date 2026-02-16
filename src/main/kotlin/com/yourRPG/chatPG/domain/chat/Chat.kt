package com.yourRPG.chatPG.domain.chat

import com.github.f4b6a3.uuid.UuidCreator
import com.yourRPG.chatPG.domain.message.Message
import com.yourRPG.chatPG.domain.poll.Poll
import com.yourRPG.chatPG.domain.account.Account
import com.yourRPG.chatPG.service.ai.providers.AiModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

//Since the IDE does not check if the class is implicitly open because of @Entity decorator, we shall suppress the
// misleading warning.
@Suppress("ProtectedInFinal")

@Entity
@Table(name = "chat")
class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Long? = null
        protected set

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    var publicId: UUID = UuidCreator.getRandomBased()
        protected set

    @Column(nullable = false)
    var name: String? = null
        protected set

    @Column(name = "owner_id")
    var ownerId: Long? = null
        protected set

    @ManyToMany
    @JoinTable(
        name = "account_chat",
        joinColumns = [JoinColumn(name = "account_id")],
        inverseJoinColumns = [JoinColumn(name = "chat_id")]
    )
    var accounts: MutableSet<Account> = HashSet()
        protected set

    @OneToMany(mappedBy = "chat")
    var messages: MutableList<Message>? = null
        protected set

    @Enumerated(EnumType.STRING)
    var model: AiModel = AiModel.NONE

    @OneToMany(mappedBy = "chat")
    var polls: MutableList<Poll>? = null
        protected set

    constructor(name: String, model: AiModel, vararg accounts: Account) {
        this.name = name
        this.model = model
        this.accounts.addAll(listOf(*accounts))
    }

    constructor(name: String, vararg accounts: Account) {
        this.name = name
        this.accounts.addAll(listOf(*accounts))
    }

    protected constructor()

}