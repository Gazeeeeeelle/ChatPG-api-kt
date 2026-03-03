package com.chatpg.security.token

import com.chatpg.dto.auth.TokenDto

data class AccessAndRefreshTokens(
    val accessToken: TokenDto,
    val refreshToken: String
) {
    constructor(accessToken: String, refreshToken: String) : this(TokenDto(accessToken), refreshToken)
}
