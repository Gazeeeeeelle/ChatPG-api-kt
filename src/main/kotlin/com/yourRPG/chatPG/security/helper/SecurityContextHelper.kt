package com.yourRPG.chatPG.security.helper

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityContextHelper {

    fun getPrincipal(): Long = getContextAuth().principal as Long

    fun setPrincipal(id: Long?, authorities: Collection<GrantedAuthority>) {
        setContextAuth(
            UsernamePasswordAuthenticationToken(id, null, authorities)
        )
    }

    fun getContextAuth(): Authentication = SecurityContextHolder.getContext().authentication

    fun setContextAuth(auth: Authentication) {
        SecurityContextHolder.getContext().authentication = auth
    }

}