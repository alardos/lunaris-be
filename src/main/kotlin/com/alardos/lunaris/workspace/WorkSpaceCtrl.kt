package com.alardos.lunaris.workspace

import com.alardos.lunaris.auth.model.User
import com.github.michaelbull.result.fold
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/workspace")
class WorkSpaceCtrl(
    @Autowired val adapter: WorkspaceAdapter
) {

    @GetMapping()
    fun get(@PathVariable("workspace") workspaceId: String): String {
        return workspaceId
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