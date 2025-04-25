package com.alardos.lunaris.card

import com.alardos.lunaris.card.model.Card
import com.github.michaelbull.result.fold
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/c/{card}")
class CardCtrl(@Autowired val adapter: CardAdapter) {



    @GetMapping()
    fun find(
        @PathVariable card: UUID,
    ): ResponseEntity<Card> =
        this.adapter.find(card)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
            ?:run { ResponseEntity(HttpStatus.NOT_FOUND) }


    @PutMapping
    fun update(@PathVariable card: UUID, @RequestBody body: Card): ResponseEntity<Any> {
        if (card != body.id) return ResponseEntity(HttpStatus.BAD_REQUEST)
        return this.adapter.update(body).fold(
            { card -> ResponseEntity(card, HttpStatus.OK) },
            { error -> ResponseEntity(error, HttpStatus.BAD_REQUEST) },
        )
    }


}