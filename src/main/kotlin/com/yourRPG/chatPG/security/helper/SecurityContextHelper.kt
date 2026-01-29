package com.yourRPG.chatPG.security.helper

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityContextHelper {

    fun getPrincipal(): Long = getContextAuth().principal as Long

    fun setPrincipal(id: Long?, authorities: Collection<GrantedAuthority>) {
        getContext().authentication =
            UsernamePasswordAuthenticationToken(id, null, authorities)
    }

    fun getContextAuth(): Authentication = getContext().authentication

    /**
     * Wraps the occurrence of statically accessing SecurityContextHolder's context.
     *
     * @return [SecurityContext] held by [SecurityContextHolder].
     */
    private fun getContext(): SecurityContext = SecurityContextHolder.getContext()

}