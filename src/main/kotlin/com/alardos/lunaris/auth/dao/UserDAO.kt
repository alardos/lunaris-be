package com.alardos.lunaris.auth.dao

import com.alardos.lunaris.auth.model.User
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class UserDAO(@Autowired val jdbi: Jdbi) {
    val tokenDAO = TokenDAO(jdbi)

    fun find(id: String): User? {
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

    fun insert(user: User): User {
        return jdbi.withHandle<User, Exception> { handle ->
            handle.createQuery("""insert into users(email,password,first_name,last_name) 
                values ('${user.email}', '${user.password}', '${user.firstName}', '${user.lastName}') returning *""")
                .map(UserMapper()).first()
        }
    }

}

class UserMapper : RowMapper<User> {
    override fun map(rs: ResultSet, ctx: StatementContext): User {
        return User(
            id = rs.getObject("id", UUID::class.java),
            email = rs.getString("email"),
            password = rs.getString("password"),
            firstName = rs.getString("first_name"), // Different column name
            lastName = rs.getString("last_name"), // Different column name
            HashSet(),
        )
    }
}
