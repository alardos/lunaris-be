package com.alardos.lunaris.workspace

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.card.dao.CardDAO
import com.alardos.lunaris.card.model.Card
import com.alardos.lunaris.card.model.CardCandidate
import com.alardos.lunaris.card.model.CardStrType
import com.alardos.lunaris.core.IntTest
import com.alardos.lunaris.core.TransactionalTest
import com.alardos.lunaris.workspace.dao.WorkspaceDAO
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.*
import kotlin.test.assertTrue

class WorkspaceIntTest(
    @Autowired val mvc: MockMvc,
    @Autowired val repo: WorkspaceDAO,
    @Autowired authAdapter: AuthAdapter,
    @Autowired passwordEncoder: PasswordEncoder,
    @Autowired val cardDAO: CardDAO,
): IntTest(authAdapter, passwordEncoder) {

    fun defaultTextCard(user: User, content: String? = null, workspace: UUID? = null): Card {
        val workspace = workspace?:defaultWorkspace(user).id
        return cardDAO.insert(CardCandidate(CardStrType.text, content ?: "defaultTextCard"),user.id,workspace)
    }

    fun defaultWorkspace(user: User, name: String? = null): Workspace {
        val workspace = repo.create(user.id, WorkspaceCandidate(name?:"default workspace"))
        val card = defaultTextCard(user, workspace = workspace.id)
        return workspace
    }

    @TransactionalTest
    fun createWorkspace() {
        val auth = defaultAuth()
        val workspace = WorkspaceCandidate("MyWorkspace")
        val response = mvc.post("/create-workspace") {
            header("Authorization","Bearer " + auth.first.value)
            contentType = MediaType.APPLICATION_JSON
            content = """{
                "name":"${workspace.name}"
            }""".trimIndent()
        }.andReturn().response
        val saved = mapper.readValue<Workspace>(response.contentAsString, Workspace::class.java)
        assertEquals(201, response.status)
        Assertions.assertNotNull(repo.find(saved!!.id))
    }



    @TransactionalTest
    fun findWorkspaceDetails() {
        val auth = defaultAuth()
        val workspace = defaultWorkspace(auth.second)
        val response = mvc.get("/w/"+workspace.id) {
            header("Authorization","Bearer " + auth.first.value)
        }.andReturn().response
        val result: WorkspaceDetails = mapper.readValue(response.contentAsString)
        assertEquals(200, response.status)
        assertEquals(result.id, workspace.id)
        assertTrue(result.cards.isNotEmpty())
    }

    @TransactionalTest
    fun getMyWorkspaces() {
        val auth = defaultAuth()
        val workspace = defaultWorkspace(auth.second)
        val response = mvc.get("/mine") {
            header("Authorization","Bearer " + auth.first.value)
        }.andReturn().response
        val result: List<Workspace> = mapper.readValue(response.contentAsString)
        assertEquals(200, response.status)
        assertEquals(1, result.size)
        assertEquals(result[0].id, workspace.id)
    }
}