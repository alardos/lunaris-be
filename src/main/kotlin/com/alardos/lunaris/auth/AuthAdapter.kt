package com.alardos.lunaris.auth

import com.alardos.lunaris.auth.model.*
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthAdapter(
    @Autowired val repo: AuthRepo,
    @Autowired val passwordEncoder: PasswordEncoder
) {
    val serv = AuthServ(Jwts.SIG.HS256.key().build(), passwordEncoder)

    fun signup(user: User): User {
        user.password=passwordEncoder.encode(user.password)
        return repo.save(user)
    }

    fun login(cred: LoginCred): Pair<AccessToken, RefreshToken>? {
        val user = repo.findByEmail(cred.email)
        return user?.let {
            val tokens = serv.login(cred, it)
            tokens?.run { repo.store(tokens.second) }
            tokens
        }
    }

    fun refresh(token: RefreshToken): Pair<AccessToken, RefreshToken>? {
        return repo.find(token)?.let {
            repo.invalidate(token)
            serv.subjectOf(token)?.let { uuid ->
                repo.findUser(uuid)?.let { user ->
                    val tokens = serv.issueFor(user)
                    repo.store(tokens.second)
                    return@let tokens
                }
            }
        }
    }

    fun canAccessWorkspace(user: User, workspaceId: String): Boolean {
        println("canAccessWorkspace")
        return true
    }

    fun subjectOf(token: Token): User? {
        return if (serv.isValid(token)) {
            serv.subjectOf(token)?.let {
                repo.findUser(it)
            }
        } else {
            null
        }
    }

}