package com.alardos.lunaris.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthCtrl(@Autowired val adapter: AuthAdapter) {
    data class TokenResponse(val accessToken: String, val refreshToken: String)

    @PostMapping("/signup")
    fun signup(@RequestBody body: User) {
        return adapter.signup(body)
    }

    @PostMapping("/login")
    fun login(@RequestBody body: LoginCred): TokenResponse? {
        return adapter.login(body)?.let { TokenResponse(it.first.value,it.second.value) }
    }

    @GetMapping("/get/{id}")
    fun get(@PathVariable id: String): User? {
        return adapter.findUser(id)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody refreshToken: String): TokenResponse? {
        return adapter.refresh(RefreshToken(refreshToken))?.let {
            TokenResponse(it.first.value,it.second.value)
        }

    }

}
