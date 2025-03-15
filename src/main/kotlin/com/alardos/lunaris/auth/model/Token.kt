package com.alardos.lunaris.auth.model

interface Token {
    val value: String
}
data class AccessToken(override val value: String): Token

data class RefreshToken(override val value: String): Token
