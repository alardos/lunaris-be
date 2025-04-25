package com.alardos.lunaris.card

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.core.TransactionalTest
import com.alardos.lunaris.workspace.WorkspaceIntTest
import com.alardos.lunaris.workspace.WorkspaceRepo
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

class CardIntTest(
    @Autowired mvc: MockMvc,
    @Autowired cardRepo: CardRepo,
    @Autowired val workspaceRepo: WorkspaceRepo,
    @Autowired authAdapter: AuthAdapter,
    @Autowired passwordEncoder: PasswordEncoder,
): WorkspaceIntTest(mvc, workspaceRepo, authAdapter,passwordEncoder,cardRepo) {
    val serv = CardServ()

    @TransactionalTest
    fun createTextCard() {
        val auth = defaultAuth()
        val candidate = CardCandidate(CardStrType.text, "")
        val workspace = defaultWorkspace(auth.second)
        val response = mvc.post("/w/${workspace.id}/create-card") {
            header("Authorization","Bearer " + auth.first.value)
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(candidate)

        }.andReturn().response
        val saved = mapper.readValue<TextCard>(response.contentAsString, TextCard::class.java)
        assertEquals(201, response.status)
        assertNotNull(cardRepo.find(saved!!.id))
    }

    @TransactionalTest
    fun find() {
        val auth = defaultAuth()
        val card = defaultTextCard(auth.second)
        val response = mvc.get("/c/${card.id}") {
            header("Authorization","Bearer " + auth.first.value)
        }.andReturn().response
        val result: TextCard = mapper.readValue(response.contentAsString)
        assertEquals(200, response.status)
        assertEquals(result.id, card.id)
    }

    @TransactionalTest
    fun update() {
        val auth = defaultAuth()
        val card = defaultTextCard(auth.second)
        (card as TextCard).content = "updated"
        val response = mvc.put("/c/${card.id}") {
            header("Authorization","Bearer " + auth.first.value)
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(card)
        }.andReturn().response
        val result: TextCard = mapper.readValue(response.contentAsString)
        assertEquals(200, response.status)
        assertEquals(result.id, card.id)
        assertEquals(card.content, (cardRepo.find(card.id)as TextCard).content)
        assertEquals(card.content, result.content)
    }
}