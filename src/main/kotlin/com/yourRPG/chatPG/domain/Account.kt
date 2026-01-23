package com.yourRPG.chatPG.domain

import com.yourRPG.chatPG.service.account.AccountStatus
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.util.UUID
import javax.validation.constraints.NotNull

//Since the IDE does not check if the class is implicitly open because of @Entity decorator, we shall suppress the
// misleading warning.
@Suppress("ProtectedInFinal")

@Entity
@SQLRestriction("status = 'ENABLED'")
class Account: UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null
        protected set

    @Column(unique = true, nullable = false)
    var name: String? = null
        protected set

    @Email
    @Column(unique = true, nullable = false)
    var email: String? = null

    @NotNull
    @Column(name = "password")
    var accountPassword: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: AccountStatus = AccountStatus.DISABLED

    @Column(unique = true)
    var uuid: UUID? = null

    @Column(unique = true)
    var uuidBirth: Instant? = null

    @Column(unique = true)
    var refreshToken: String? = null

    @ManyToMany(mappedBy = "accounts")
    var chats: MutableList<Chat>? = null
        protected set

    @OneToMany(mappedBy = "account")
    var messages: MutableList<Message>? = null
        protected set

    constructor(name: String, email: String, password: String) {
        this.name = name
        this.email = email
        this.accountPassword = password
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String? {
        return accountPassword
    }

    override fun getUsername(): String? {
        return name
    }

}