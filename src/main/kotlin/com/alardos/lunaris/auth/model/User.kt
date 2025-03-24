package com.alardos.lunaris.auth.model

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.springframework.security.core.GrantedAuthority
import java.sql.ResultSet
import java.util.*

class User(
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    val authorities: Collection<GrantedAuthority?>?,
) {
    lateinit var id: UUID

    constructor(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        ): this(email, password, firstName,lastName, HashSet()) {
    }

    constructor(
        id: UUID,
        email: String,
        password: String,
        firstName: String,
        lastName: String,

    ): this(email, password, firstName,lastName, HashSet()) {
        this.id = id
    }

    constructor(
        id: UUID,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        authorities: HashSet<GrantedAuthority>,
    ): this(email, password, firstName,lastName, authorities) {
        this.id = id
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
