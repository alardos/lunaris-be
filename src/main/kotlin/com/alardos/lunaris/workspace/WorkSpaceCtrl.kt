package com.alardos.lunaris.workspace

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/w/{workspace}")
class WorkSpaceCtrl {
    @GetMapping()
    fun get(@PathVariable("workspace") workspaceId: String): String {
        return workspaceId
    }
}