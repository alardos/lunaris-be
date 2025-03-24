package com.alardos.lunaris.workspace.card

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.card.*
import com.alardos.lunaris.workspace.WorkspaceRepo
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class CardIntTest(
    @Autowired mvc: MockMvc,
    @Autowired val cardRepo: CardRepo,
    @Autowired val workspaceRepo: WorkspaceRepo,
    @Autowired authAdapter: AuthAdapter,
    @Autowired passwordEncoder: PasswordEncoder,
): WorkspaceIntTest(mvc, workspaceRepo, authAdapter,passwordEncoder) {
    val serv = CardServ()

    fun defaultTextCard(user: User, name: String? = null): Card {
        val workspace = defaultWorkspace(user)
        return cardRepo.insert(CardCandidate(CardStrType.text,name?:"defaultTextCard"),user.id,workspace.id)

    }

    @Test fun createTextCard() {
        val auth = defaultAuth()
        val candidate = CardCandidate(CardStrType.text,"")
        val workspace = defaultWorkspace(auth.second)
        val response = mvc.post("/w/${workspace.id}/card/create") {
            header("Authorization","Bearer " + auth.first.value)
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(candidate)

        }.andReturn().response
        val saved = mapper.readValue<TextCard>(response.contentAsString, TextCard::class.java)
        assertEquals(201, response.status)
        assertNotNull(cardRepo.find(saved!!.id))
    }

    @Test fun find() {
        val auth = defaultAuth()
        val card = defaultTextCard(auth.second)
        val response = mvc.get("/w/${card.workspace}/card/${card.id}") {
            header("Authorization","Bearer " + auth.first.value)
        }.andReturn().response
        val result: TextCard = mapper.readValue(response.contentAsString)
        assertEquals(200, response.status)
        assertEquals(result.id,card.id)
    }
}