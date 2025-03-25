package com.alardos.lunaris.core

import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.workspace.Workspace
import com.alardos.lunaris.workspace.WorkspaceAdapter
import com.alardos.lunaris.workspace.WorkspaceCandidate
import com.github.michaelbull.result.fold
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CoreCtrl(
    @Autowired val workspaceAdapter: WorkspaceAdapter
) {
    @GetMapping()
    fun default(): String {
        return "Hello World!"
    }

    @PostMapping("/create-workspace")
    fun create(@AuthenticationPrincipal creator: User, @RequestBody workspace: WorkspaceCandidate): ResponseEntity<*> {
        return workspaceAdapter.create(creator, workspace).fold(
            { workspace -> ResponseEntity(workspace, HttpStatus.CREATED) },
            { error -> ResponseEntity(error, HttpStatus.BAD_REQUEST) }
        )
    }

    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal owner: User): List<Workspace> {
        return workspaceAdapter.findByOwner(owner.id)
    }

}