package com.alardos.lunaris.auth.dao

import com.alardos.lunaris.auth.model.RefreshToken
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class TokenDAO(@Autowired val jdbi: Jdbi) {
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

    fun invalidate(token: RefreshToken): Unit {
        return jdbi.useHandle<Exception> { handle ->
            handle.execute("delete from refresh_tokens where token = '${token.value}'")
        }
    }

}