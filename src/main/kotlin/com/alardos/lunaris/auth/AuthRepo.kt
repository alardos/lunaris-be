package com.alardos.lunaris.auth

import com.alardos.lunaris.auth.model.RefreshToken
import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.auth.model.UserMapper
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class AuthRepo(@Autowired val jdbi: Jdbi) {

    fun findUser(id: String): User? {
        return jdbi.withHandle<User?, Exception> { handle ->
            handle.createQuery("select * from public.users where id = '$id'")
                .map(UserMapper()).findOne().orElse(null)
        }
    }

    fun findByEmail(email: String): User? {
        return jdbi.withHandle<User?, Exception> { handle ->
            handle.createQuery("select * from public.users where email = '$email'")
                .map(UserMapper()).findOne().orElse(null)
        }
    }

    fun save(user: User): User {
        return jdbi.withHandle<User, Exception> { handle ->
            handle.createQuery("""insert into users(email,password,first_name,last_name) 
                values ('${user.email}', '${user.password}', '${user.firstName}', '${user.lastName}') returning *""")
                .map(UserMapper()).first()
        }
    }

    fun store(token: RefreshToken) {
        jdbi.useHandle<Exception> { handle ->
            handle.execute("insert into refresh_tokens(token) values ('${token.value}');")
        }
    }

    fun find(token: RefreshToken): RefreshToken? {
        return jdbi.withHandle<RefreshToken?, Exception> { handle ->
            val result = handle.createQuery("select token from refresh_tokens where token = '${token.value}';")
                .mapTo(String::class.java).findOne().orElse(null)?.let { RefreshToken(it) }
            result
        }
    }

    fun invalidate(token: RefreshToken) {
        return jdbi.useHandle<Exception> { handle ->
            handle.execute("delete from refresh_tokens where token = '${token.value}'")
        }
    }
}