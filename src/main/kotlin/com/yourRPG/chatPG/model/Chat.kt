package com.yourRPG.chatPG.model

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
import java.util.*


@Entity
@Table(name = "chat")
class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private val id: Long? = null

    @Column(nullable = false)
    private var name: String? = null

    @Column(name = "owner_id")
    private var ownerId: Long? = null

    @ManyToMany
    @JoinTable(
        name = "account_chat",
        joinColumns = [JoinColumn(name = "account_id")],
        inverseJoinColumns = [JoinColumn(name = "chat_id")]
    )
    private var accounts: MutableSet<Account?> = HashSet<Account?>()

    @OneToMany(mappedBy = "chat")
    private var messages: MutableList<Message?>? = null

    @Enumerated(EnumType.STRING)
    private var model: AiModel = AiModel.NONE

    @OneToMany(mappedBy = "chat")
    private var polls: MutableList<Poll?>? = null

    constructor(name: String?, model: AiModel, vararg accounts: Account?) {
        this.name = name
        this.model = model
        this.accounts.addAll(Arrays.asList<Account?>(*accounts))
    }

    constructor(name: String?, vararg accounts: Account?) {
        this.name = name
        this.accounts.addAll(Arrays.asList<Account?>(*accounts))
    }

    constructor()

    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Chat {
        this.name = name
        return this
    }

    fun getOwnerId(): Long? {
        return ownerId
    }

    fun setOwnerId(ownerId: Long?): Chat {
        this.ownerId = ownerId
        return this
    }

    fun getId(): Long? {
        return id
    }

    fun getModel(): AiModel {
        return model
    }

    fun setModel(model: AiModel): Chat {
        this.model = model
        return this
    }

    fun getAmountOfAccounts(): Int {
        return accounts.size
    }

}