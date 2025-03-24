package com.alardos.lunaris.card

import com.alardos.lunaris.auth.model.User
import com.github.michaelbull.result.fold
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/w/{workspace}/card")
class CardCtrl(@Autowired val adapter: CardAdapter) {

    @PostMapping("/create")
    fun create(
        @AuthenticationPrincipal creator: User,
        @PathVariable workspace: UUID,
        @RequestBody body: CardCandidate
    ): ResponseEntity<Card> =
        this.adapter.create(body, creator.id, workspace)
            ?.let { ResponseEntity(it, HttpStatus.CREATED) }
            ?:run { ResponseEntity(HttpStatus.BAD_REQUEST) }


    @GetMapping("/{card}")
    fun find(
        @PathVariable card: UUID,
    ): ResponseEntity<Card> =
        this.adapter.find(card)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
            ?:run { ResponseEntity(HttpStatus.NOT_FOUND) }


    @PutMapping
    fun update(@PathVariable workspace: UUID, @RequestBody body: Card): ResponseEntity<*> =
        this.adapter.update(workspace, body).fold(
            { card -> ResponseEntity(card, HttpStatus.OK) },
            { error -> ResponseEntity(error, HttpStatus.BAD_REQUEST) },
        )


    @GetMapping("/all")
    fun all(@PathVariable workspace: UUID): List<Card> =
        this.adapter.forWorkspace(workspace)


}