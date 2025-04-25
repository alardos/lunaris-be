package com.alardos.lunaris.core

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.auth.model.AccessToken
import com.alardos.lunaris.auth.model.LoginCred
import com.alardos.lunaris.auth.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers(parallel = true)
class IntTest(
    @Autowired val authAdapter: AuthAdapter,
    @Autowired val passwordEncoder: PasswordEncoder,
) {
    val mapper: ObjectMapper = jacksonObjectMapper().registerKotlinModule()

    companion object {
        @Container @JvmStatic
        val container: PostgreSQLContainer<*> = PostgreSQLContainer("postgres").withReuse(true)

        @DynamicPropertySource @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { println("getting datasource url"); container.jdbcUrl }
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }
    }

    fun defaultAuth(): Pair<AccessToken,User> {
        val password = "password"
        var user = User("test@test.com", password, "fname", "lname")
        user = authAdapter.signup(user)

        return AccessToken(authAdapter.login(LoginCred(user.email,password))!!.accessToken) to user
    }

}

@Test
@Transactional
annotation class TransactionalTest
