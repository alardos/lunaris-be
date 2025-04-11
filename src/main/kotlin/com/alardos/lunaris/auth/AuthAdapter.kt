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

    fun login(cred: LoginCred): TokenResponse? {
        val user = repo.findByEmail(cred.email)
        return user?.let {
            val tokens = serv.login(cred, it)
            tokens?.let {
                repo.store(tokens.second)
                TokenResponse(tokens.first.value,tokens.second.value,user.id.toString())
            }
        }
    }

    fun refresh(token: RefreshToken): TokenResponse? {
        return repo.find(token)?.let {
            repo.invalidate(token)
            serv.subjectOf(token)?.let { uuid ->
                repo.findUser(uuid)?.let { user ->
                    val tokens = serv.issueFor(user,serv.expDateOf(token))
                    repo.store(tokens.second)
                    return@let TokenResponse(tokens.first.value,tokens.second.value,user.id.toString())
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