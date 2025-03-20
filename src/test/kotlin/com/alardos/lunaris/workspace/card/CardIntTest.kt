package com.alardos.lunaris.workspace.card

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.core.IntTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc

class CardIntTest(
    @Autowired val mvc: MockMvc,
    @Autowired authAdapter: AuthAdapter,
    @Autowired passwordEncoder: PasswordEncoder,
): IntTest(authAdapter,passwordEncoder) {

    @Test
    fun createDefaultCard() {

    }
}