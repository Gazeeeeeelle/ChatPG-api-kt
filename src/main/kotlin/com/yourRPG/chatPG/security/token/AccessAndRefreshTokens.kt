package com.yourRPG.chatPG.security.token

import com.yourRPG.chatPG.dto.auth.TokenDto

data class AccessAndRefreshTokens(
    val accessToken: TokenDto,
    val refreshToken: String
) {
    constructor(accessToken: String, refreshToken: String) : this(TokenDto(accessToken), refreshToken)
}
