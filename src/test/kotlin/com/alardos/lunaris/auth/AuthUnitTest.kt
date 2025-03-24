package com.alardos.lunaris.auth

import com.alardos.lunaris.auth.model.AccessToken
import com.alardos.lunaris.auth.model.LoginCred
import com.alardos.lunaris.auth.model.RefreshToken
import com.alardos.lunaris.auth.model.User
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import java.util.*
import java.util.UUID.randomUUID
import javax.crypto.SecretKey
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthUnitTest {
    val key: SecretKey = Jwts.SIG.HS256.key().build()
    val serv: AuthServ = AuthServ(key, NoOpPasswordEncoder.getInstance())
    val user = User(randomUUID(), "test@test.com", "password", "fname", "lname")
    val jwtBuilder = Jwts.builder().subject(user.id.toString())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
            .signWith(key)


    @Nested inner class IsValid {

        @Test
        fun `validate valid token`() {
            val token = AccessToken(jwtBuilder.compact())
            assertTrue(serv.isValid(token))
        }

        @Test
        fun `when expired return false`() {
            val token = AccessToken(
                jwtBuilder
                    .expiration(Date(System.currentTimeMillis() - 1000))
                    .compact()
            )
            assertFalse(serv.isValid(token))
        }

        @Test
        fun `when signature invalid`() {
            val token = AccessToken(
                jwtBuilder
                    .signWith(Jwts.SIG.HS256.key().build()) // different key
                    .compact()
            )
            assertFalse(serv.isValid(token))
        }

        @Test
        fun `non token`() {
            val token = AccessToken("SomeRandomString")
            assertFalse(serv.isValid(token))
        }

        @Test
        fun tempered() {
            val token = AccessToken(jwtBuilder.compact() + "tempering")
            assertFalse(serv.isValid(token))
        }
    }
    @Nested inner class Login {

        @Test fun `issue valid token`() {
            val (accessToken,refreshToken) = serv.login(LoginCred(user.email, user.password),user)!!
            assertDoesNotThrow { Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken.value) }
            assertDoesNotThrow { Jwts.parser().verifyWith(key).build().parseSignedClaims(refreshToken.value) }
        }

        @Test fun `is real token`() {
            val (accessToken,refreshToken) = serv.login(LoginCred(user.email, user.password),user)!!
            assertThrows<Exception> { Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken.value+"tempering") }
            assertThrows<Exception> { Jwts.parser().verifyWith(key).build().parseSignedClaims(refreshToken.value+"tempering") }
        }

        @Test fun `wrong password`() {
            assertNull(serv.login(LoginCred(user.email, "wrong password"),user))
        }
    }
    @Nested inner class IssueAccessFromRefreshToken {
        @Test fun `with valid refresh token`() {
            val validRefreshT = RefreshToken(jwtBuilder.compact())
            assertNotNull(serv.issueAccessFromRefreshToken(validRefreshT, user))
        }

        @Test fun `reject expired refresh token`() {
            val expiredRefreshT = RefreshToken(
                jwtBuilder
                    .expiration(Date(System.currentTimeMillis() - 1000))
                    .compact()
            )
            assertNull(serv.issueAccessFromRefreshToken(expiredRefreshT, user))
        }

        @Test fun `reject tempered token`() {
            val expiredRefreshT = RefreshToken(jwtBuilder.compact() + "tempering")
            assertNull(serv.issueAccessFromRefreshToken(expiredRefreshT, user))
        }

        @Test fun `new token is valid`() {
            val expiredRefreshT = RefreshToken(jwtBuilder.compact())
            val result = serv.issueAccessFromRefreshToken(expiredRefreshT, user)!!
            assertDoesNotThrow { Jwts.parser().verifyWith(key).build().parseSignedClaims(result.value) }
        }
    }
}