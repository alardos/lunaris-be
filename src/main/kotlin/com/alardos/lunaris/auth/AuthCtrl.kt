package com.alardos.lunaris.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthCtrl(@Autowired val adapter: AuthAdapter) {

    @PostMapping("/signup")
    fun signup(@RequestBody body: User) {
        return adapter.signup(body)
    }

    @PostMapping("/login")
    fun login(@RequestBody body: LoginCred): String? {
        return adapter.login(body)
    }

    @GetMapping("/get/{id}")
    fun get(@PathVariable id: String): User? {
        return adapter.findUser(id)
    }

}
