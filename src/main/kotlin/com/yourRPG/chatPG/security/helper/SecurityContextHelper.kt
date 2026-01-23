package com.yourRPG.chatPG.security.helper

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityContextHelper {

    fun getPrincipal(): Long = SecurityContextHolder.getContext().authentication.principal as Long

    fun setPrincipal(id: Long?, authorities: Collection<GrantedAuthority>) {
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(id, null, authorities)
    }

}