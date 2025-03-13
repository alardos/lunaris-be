package com.alardos.lunaris.auth

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class User(
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
) {
    lateinit var id: String
    constructor(
        id: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
    ): this(email, password, firstName,lastName) {
        this.id = id
    }

}



class UserMapper : RowMapper<User> {
    override fun map(rs: ResultSet, ctx: StatementContext): User {
        return User(
            id = rs.getString("id"),
            email = rs.getString("email"),
            password = rs.getString("password"),
            firstName = rs.getString("first_name"), // Different column name
            lastName = rs.getString("last_name"), // Different column name

        )
    }
}
