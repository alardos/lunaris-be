package com.alardos.lunaris.auth

import com.alardos.lunaris.auth.model.LoginCred
import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.core.IntTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertFalse

class AuthIntTest(
    @Autowired val repo: AuthRepo,
    @Autowired val adapter: AuthAdapter,
    @Autowired val mvc: MockMvc,
    @Autowired passwordEncoder: PasswordEncoder,
): IntTest(adapter,passwordEncoder) {

    @Test
    fun login() {
        val password = "password"
        val user = repo.save(User("test@test.com", passwordEncoder.encode(password), "fname", "lname"))
        val result = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(LoginCred(user.email, password))
        }.andReturn().response.contentAsString
        assertEquals(true, result.isNotEmpty())
    }

    @Test
    fun signup() {
        val user = User("test@test.com", "password", "fname", "lname")
        mvc.post("/auth/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = """{
                "email":"${user.email}",
                "password":"${user.password}",
                "firstName":"${user.firstName}",
                "lastName":"${user.lastName}"
            }"""
        }
        assertNotNull(repo.findByEmail(user.email))
    }

    @Test
    fun refresh() {
        val password = "password"
        val user = User("test@test.com", passwordEncoder.encode(password), "fname", "lname")
        repo.save(user)
        val tokens = adapter.login(LoginCred(user.email, password))
        val response = mvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = tokens?.second?.value
        }.andReturn().response
        assertFalse(response.contentAsString.isEmpty())
        assertTrue(response.status == 200)
    }

}