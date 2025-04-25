package com.alardos.lunaris.auth

import com.alardos.lunaris.auth.dao.UserDAO
import com.alardos.lunaris.auth.model.LoginCred
import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.core.IntTest
import com.alardos.lunaris.core.TransactionalTest
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertFalse

class AuthIntTest(
    @Autowired val repo: UserDAO,
    @Autowired val adapter: AuthAdapter,
    @Autowired val mvc: MockMvc,
    @Autowired passwordEncoder: PasswordEncoder,
): IntTest(adapter,passwordEncoder) {

    @TransactionalTest
    fun login() {
        val password = "password"
        val user = repo.insert(User("test@test.com", passwordEncoder.encode(password), "fname", "lname"))
        val result = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(LoginCred(user.email, password))
        }.andReturn().response.contentAsString
        assertEquals(true, result.isNotEmpty())
    }

    @TransactionalTest
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

    @TransactionalTest
    fun refresh() {
        val password = "password"
        val user = User("test@test.com", passwordEncoder.encode(password), "fname", "lname")
        repo.insert(user)
        val tokens = adapter.login(LoginCred(user.email, password))
        val response = mvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = tokens?.refreshToken
        }.andReturn().response
        assertFalse(response.contentAsString.isEmpty())
        assertTrue(response.status == 200)
    }

}