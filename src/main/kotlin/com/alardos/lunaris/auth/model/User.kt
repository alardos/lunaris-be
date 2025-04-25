package com.alardos.lunaris.auth.model

import org.springframework.security.core.GrantedAuthority
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



