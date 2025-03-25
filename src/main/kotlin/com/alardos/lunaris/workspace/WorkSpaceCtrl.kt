package com.alardos.lunaris.workspace

import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.card.Card
import com.alardos.lunaris.card.CardAdapter
import com.alardos.lunaris.card.CardCandidate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/w/{workspace}")
class WorkSpaceCtrl(
    @Autowired val adapter: WorkspaceAdapter,
    @Autowired val cardAdapter: CardAdapter,
) {

    @PostMapping("/create-card")
    fun create(
        @AuthenticationPrincipal creator: User,
        @PathVariable workspace: UUID,
        @RequestBody body: CardCandidate
    ): ResponseEntity<Card> =
        this.cardAdapter.create(body, creator.id, workspace)
            ?.let { ResponseEntity(it, HttpStatus.CREATED) }
            ?:run { ResponseEntity(HttpStatus.BAD_REQUEST) }


    @GetMapping()
    fun get(@PathVariable() workspace: UUID): ResponseEntity<Workspace>? {
        return adapter.findDetails(workspace)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
            ?:run { ResponseEntity(HttpStatus.NOT_FOUND) }
    }

//    @GetMapping("/all")
//    fun all(@PathVariable workspace: UUID): List<Card> =
//        cardAdapter.forWorkspace(workspace)

}