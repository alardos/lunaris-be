package com.alardos.lunaris.auth

import com.alardos.lunaris.auth.model.LoginCred
import com.alardos.lunaris.auth.model.RefreshToken
import com.alardos.lunaris.auth.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthCtrl(@Autowired val adapter: AuthAdapter) {
    data class TokenResponse(val accessToken: String, val refreshToken: String)

    @PostMapping("/signup")
    fun signup(@RequestBody body: User) {
        adapter.signup(body)
    }

    @PostMapping("/login")
    fun login(@RequestBody body: LoginCred): TokenResponse? {
        return adapter.login(body)?.let { TokenResponse(it.first.value,it.second.value) }
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody refreshToken: String): TokenResponse? {
        return adapter.refresh(RefreshToken(refreshToken))?.let {
            TokenResponse(it.first.value,it.second.value)
        }

    }

}
