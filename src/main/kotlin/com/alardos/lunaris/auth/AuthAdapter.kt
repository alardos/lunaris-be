package com.alardos.lunaris.auth

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class AuthAdapter(
    @Autowired val repo: AuthRepo,
    @Autowired val passwordEncoder: PasswordEncoder
) {
    val key: SecretKey = Jwts.SIG.HS256.key().build()
    fun findUser(id: String) = repo.findUser(id)

    fun signup(user: User) {
        user.password=passwordEncoder.encode(user.password)
        repo.save(user)
    }

    fun login(cred: LoginCred): String? {
        val user = repo.findByEmail(cred.email)
        return user?.let {
            if (passwordEncoder.matches(cred.password, it.password)) {
                return@let Jwts.builder().subject(user.id).issuedAt(Date())
                    .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
                    .signWith(key)
                    .compact()
            } else {
                return@let null
            }

        }
    }
}