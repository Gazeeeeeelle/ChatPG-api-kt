package com.yourRPG.chatPG.domain.account

import com.github.f4b6a3.uuid.UuidCreator
import com.yourRPG.chatPG.domain.chat.Chat
import com.yourRPG.chatPG.domain.message.Message
import com.yourRPG.chatPG.service.account.AccountStatus
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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(updatable = false)
    var id: Long? = null
        protected set

    //UuidCreator.getRandomBased outperforms java.util.UUID.randomUUID
    @Column(unique = true, nullable = false, updatable = false)
    var publicId: UUID = UuidCreator.getRandomBased()

    @Column(unique = true, nullable = false, updatable = false)
    var name: String? = null
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: AccountStatus = AccountStatus.DISABLED

    @Embedded
    var auth: AccountAuth = AccountAuth()

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

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String? {
        return auth.credentials.password
    }

    override fun getUsername(): String? {
        return name
    }

}