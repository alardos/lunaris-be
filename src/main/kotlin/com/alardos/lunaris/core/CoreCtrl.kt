package com.alardos.lunaris.core

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CoreCtrl {

    @GetMapping()
    fun default(): String {
        return "Hello World!"
    }
}