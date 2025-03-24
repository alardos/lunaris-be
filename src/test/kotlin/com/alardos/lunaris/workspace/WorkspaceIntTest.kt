package com.alardos.lunaris.workspace

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.core.IntTest
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class WorkspaceIntTest(
    @Autowired val mvc: MockMvc,
    @Autowired val repo: WorkspaceRepo,
    @Autowired authAdapter: AuthAdapter,
    @Autowired passwordEncoder: PasswordEncoder,
): IntTest(authAdapter, passwordEncoder) {

    fun defaultWorkspace(user: User, name: String? = null): Workspace {
        return repo.create(user.id, WorkspaceCandidate(name?:"default workspace"))
    }

    @Test
    fun createWorkspace() {
        val auth = defaultAuth()
        val workspace = WorkspaceCandidate("MyWorkspace")
        val response = mvc.post("/workspace/create") {
            header("Authorization","Bearer " + auth.first.value)
            contentType = MediaType.APPLICATION_JSON
            content = """{
                "name":"${workspace.name}"
            }""".trimIndent()
        }.andReturn().response
        val saved = mapper.readValue<Workspace>(response.contentAsString, Workspace::class.java)
        Assertions.assertEquals(201, response.status)
        Assertions.assertNotNull(repo.find(saved!!.id))
    }

    @Test
    fun findWorkspace() {
        val auth = defaultAuth()
        val workspace = defaultWorkspace(auth.second)
        val response = mvc.get("/workspace/"+workspace.id) {
            header("Authorization","Bearer " + auth.first.value)
        }.andReturn().response
        val result: Workspace = mapper.readValue(response.contentAsString)
        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(result.id, workspace.id)
    }

    @Test
    fun getMyWorkspaces() {
        val auth = defaultAuth()
        val workspace = defaultWorkspace(auth.second)
        val response = mvc.get("/workspace/mine") {
            header("Authorization","Bearer " + auth.first.value)
        }.andReturn().response
        val result: List<Workspace> = mapper.readValue(response.contentAsString)
        Assertions.assertEquals(200, response.status)
        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(result[0].id, workspace.id)
    }
}