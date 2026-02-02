package com.yourRPG.chatPG.domain.account

import com.yourRPG.chatPG.domain.chat.Chat
import com.yourRPG.chatPG.domain.message.Message
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

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
        this.auth.credentials.email = email
        this.auth.credentials.accountPassword = password
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String? {
        return auth.credentials.accountPassword
    }

    override fun getUsername(): String? {
        return name
    }

}