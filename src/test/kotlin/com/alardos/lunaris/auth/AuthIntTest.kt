package com.alardos.lunaris.auth

import com.alardos.lunaris.core.IntTest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class AuthIntTest(
    @Autowired val repo: AuthRepo,
    @Autowired val mvc: MockMvc,
    @Autowired val passwordEncoder: PasswordEncoder,
): IntTest() {
    val mapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `can get user info`() {
        val user = repo.save(User("","","","fname","lname"))
        val result = mvc.get("/auth/get/${user.id}").andReturn().response.contentAsString
        assertEquals(true, result.contains(""""firstName":"fname""""))
    }

    @Test
    fun login() {
        val password = "password"
        val user = repo.save(User("test@test.com",passwordEncoder.encode(password),"fname","lname"))
        val result = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(LoginCred(user.email,password))
        }.andReturn().response.contentAsString
        assertEquals(true, result.isNotEmpty())
    }

    @Test
    fun signup() {
        val user = User("test@test.com","password","fname","lname")
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

    // todo: test for refresh
}