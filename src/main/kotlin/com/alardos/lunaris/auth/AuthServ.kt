package com.alardos.lunaris.auth

import com.alardos.lunaris.auth.model.*
import io.jsonwebtoken.Jwts
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import javax.crypto.SecretKey

const val ACCESS_TOKEN_LIFETIME: Long = 1000 * 60 * 60 * 30
const val REFRESH_TOKEN_LIFETIME: Long = 1000 * 60 * 60 * 24 * 7

class AuthServ(val secret: SecretKey, val encoder: PasswordEncoder) {


    fun login(cred: LoginCred, user: User): Pair<AccessToken, RefreshToken>? {
        return if (encoder.matches(cred.password, user.password)) {
            return issueFor(user)
        } else {
            return null
        }
    }

    fun isValid(token: Token): Boolean {
        return Jwts.parser().verifyWith(secret).build()
            .runCatching { parseSignedClaims(token.value) }
            .fold(
                { it.payload.expiration.after(Date()) },
                { false }
            )
    }

    fun issueAccessFromRefreshToken(refreshToken: RefreshToken, user: User): AccessToken? {
        return if (isValid(refreshToken)) {
            return AccessToken(
                Jwts.builder().subject(user.id.toString()).issuedAt(Date())
                    .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
                    .signWith(secret)
                    .compact()
            )
        } else {
            null
        }
    }

    /** null when invalid token */
    fun subjectOf(token: Token):String? {
        return Jwts.parser().verifyWith(secret).build()
            .runCatching { parseSignedClaims(token.value) }
            .getOrNull()?.payload?.subject
    }

    fun expDateOf(token: Token):Date? {
        return Jwts.parser().verifyWith(secret).build()
            .runCatching { parseSignedClaims(token.value) }
            .getOrNull()?.payload?.expiration
    }

    fun issueFor(user: User, absoluteExpDate:Date?=null): Pair<AccessToken, RefreshToken> {
        return Pair(
            AccessToken(genToken(user, ACCESS_TOKEN_LIFETIME)),
            RefreshToken(genToken(user, absoluteExpDate?.time?:REFRESH_TOKEN_LIFETIME))
        )
    }

    private fun genToken(user: User, lifetime: Long): String {
        return Jwts.builder().subject(user.id.toString()).issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + lifetime))
            .signWith(secret)
            .compact()
    }


}