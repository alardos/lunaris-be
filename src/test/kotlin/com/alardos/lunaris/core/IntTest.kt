package com.alardos.lunaris.core

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.auth.model.AccessToken
import com.alardos.lunaris.auth.model.LoginCred
import com.alardos.lunaris.auth.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.AfterEach
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
import org.testcontainers.containers.PostgreSQLContainer


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntTest(
    @Autowired val authAdapter: AuthAdapter,
    @Autowired val passwordEncoder: PasswordEncoder,
) {
    val mapper: ObjectMapper = jacksonObjectMapper().registerKotlinModule()

    companion object {
        val container: PostgreSQLContainer<*> = PostgreSQLContainer("postgres").withReuse(false)

        @DynamicPropertySource @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            container.start()
            registry.add("spring.datasource.url") { println("getting datasource url"); container.getJdbcUrl() }
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }
    }

    @AfterEach
    fun teardown() { container.stop() }

    fun defaultAuth(): Pair<AccessToken,User> {
        val password = "password"
        var user = User("test@test.com", password, "fname", "lname")
        user = authAdapter.signup(user)

        return authAdapter.login(LoginCred(user.email,password))!!.first to user
    }

}