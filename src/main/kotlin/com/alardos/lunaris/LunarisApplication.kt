package com.alardos.lunaris

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LunarisApplication

fun main(args: Array<String>) {
    runApplication<LunarisApplication>(*args)
}
