package com.yourRPG.chatPG.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.validation.constraints.NotNull

@Suppress("ProtectedInFinal")

@Entity
@Table(name = "account")
class Account: UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null
        protected set

    @Column(unique = true, nullable = false)
    var name: String? = null
        protected set

    @NotNull
    @Column(name = "password")
    var accountPassword: String? = null

    @ManyToMany(mappedBy = "accounts")
    var chats: MutableList<Chat>? = null
        protected set

    @OneToMany(mappedBy = "account")
    var messages: MutableList<Message>? = null
        protected set

    protected constructor()

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