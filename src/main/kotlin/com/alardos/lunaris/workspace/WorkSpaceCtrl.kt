package com.alardos.lunaris.workspace

import com.alardos.lunaris.auth.model.User
import com.github.michaelbull.result.fold
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/workspace")
class WorkSpaceCtrl(
    @Autowired val adapter: WorkspaceAdapter
) {

    @GetMapping("/{workspace}")
    fun get(@PathVariable() workspace: UUID): ResponseEntity<Workspace>? {
        println(workspace)
        return adapter.find(workspace)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
            ?:run { ResponseEntity(HttpStatus.NOT_FOUND) }
    }

    @PostMapping("/create")
    fun create(@AuthenticationPrincipal creator: User, @RequestBody workspace: WorkspaceCandidate): ResponseEntity<*> {
        return adapter.create(creator, workspace).fold(
            { workspace -> ResponseEntity(workspace, HttpStatus.CREATED) },
            { error -> ResponseEntity(error, HttpStatus.BAD_REQUEST) }
        )
    }

    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal owner: User): List<Workspace> {
        return adapter.findByOwner(owner.id)
    }
}