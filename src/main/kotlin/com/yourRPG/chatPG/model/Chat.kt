package com.yourRPG.chatPG.model

import com.yourRPG.chatPG.service.ai.providers.AiModel
import jakarta.persistence.*

/**
 * Since the IDE does not check if the account is implicitly open because of @Entity decorator, we shall suppress the
 * misleading warning.
 */
@Suppress("ProtectedInFinal")

@Entity
@Table(name = "chat")
class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    var id: Long? = null
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