package com.chatpg.domain.account

import com.github.f4b6a3.uuid.UuidCreator
import com.chatpg.domain.chat.Chat
import com.chatpg.domain.message.Message
import com.chatpg.exception.account.AccountPasswordNotFoundException
import com.chatpg.exception.account.AccountUsernameNotFoundException
import com.chatpg.logging.LoggingUtils
import com.chatpg.service.account.AccountStatus
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

//Since the IDE does not check if the class is implicitly open because of @Entity decorator, we shall suppress the
// misleading warning.
@Suppress("ProtectedInFinal")

@Entity
@SQLRestriction("status = 'ENABLED'")
class Account: UserDetails {

    private companion object {
        val log = LoggingUtils(this)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true, updatable = false)
    var id: Long? = null

    //UuidCreator.getRandomBased outperforms java.util.UUID.randomUUID concurrently
    @Column(nullable = false, unique = true, updatable = false)
    var publicId: UUID = UuidCreator.getRandomBased()

    @Column(nullable = false, unique = true, updatable = false)
    var name: String? = null
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: AccountStatus = AccountStatus.DISABLED

    @Embedded
    var auth: AccountAuth = AccountAuth()
        protected set

    @ManyToMany(mappedBy = "accounts")
    var chats: MutableList<Chat>? = null
        protected set

    @OneToMany(mappedBy = "account")
    var messages: MutableList<Message>? = null
        protected set

    constructor(name: String, email: String, password: String) {
        this.name = name
        this.auth.credentials.apply {
            this.email = email
            this.password = password
        }
    }

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun getPassword(): String =
        auth.credentials.password ?: log.logAndThrow {
            AccountPasswordNotFoundException()
        }

    override fun getUsername(): String =
        name ?: log.logAndThrow {
            AccountUsernameNotFoundException()
        }

}