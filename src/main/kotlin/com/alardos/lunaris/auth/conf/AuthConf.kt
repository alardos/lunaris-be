package com.alardos.lunaris.auth.conf

import com.alardos.lunaris.auth.AuthAdapter
import com.alardos.lunaris.auth.model.AccessToken
import com.alardos.lunaris.auth.model.User
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.regex.Pattern


@Configuration
@EnableWebSecurity(debug = false)
class AuthConf(
    @Autowired val jwtFilter: JwtTokenFilter,
    @Autowired val authAdapter: AuthAdapter,
) {


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf { csrf -> csrf.disable() }
        http.anonymous{ a -> a.disable() }

        return http
            .addFilterAfter(jwtFilter, BasicAuthenticationFilter::class.java)
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/auth/login").permitAll()
                    .requestMatchers("/auth/refresh").permitAll()
                    .requestMatchers("/auth/signup").permitAll()
                    .requestMatchers("/w/**").access { supplier, context ->
                        val authentication = supplier.get() as JwtAuthentication // throws
                        var workspace: String? = null
                        val matcher = Pattern.compile("/w/([^/]+)").matcher(context.request.requestURI)
                        if (matcher.find()) { workspace = matcher.group(1) }
                        val user = authentication.principal
                        AuthorizationDecision(
                            authentication.isAuthenticated &&
                            workspace is String &&
                            authAdapter.canAccessWorkspace(user, workspace)
                        )
                    }
                    .anyRequest().authenticated()
            }
            .build()
    }

}

@Component
class JwtTokenFilter(@Autowired val authAdapter: AuthAdapter) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authorizationHeader: String? = request.getHeader("Authorization")
        if (authorizationHeader == null || authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = AccessToken(authorizationHeader.substring(7))
        val user: User = authAdapter.subjectOf(token)?: run {
            filterChain.doFilter(request, response)
            return
        }

        SecurityContextHolder.getContext().authentication = JwtAuthentication(user, true)

        filterChain.doFilter(request, response)
    }

}

class JwtAuthentication(private val user: User, private var authenticated: Boolean = false): Authentication {
    override fun getAuthorities() = user.authorities


    override fun getCredentials(): Any? {
        TODO("Not yet implemented")
    }

    override fun getDetails(): Any? {
        TODO("Not yet implemented")
    }

    override fun getPrincipal(): User = user

    /** if this class is used then the authentication already happened */
    override fun isAuthenticated(): Boolean = authenticated

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated=isAuthenticated
    }

    override fun getName(): String = user.email


}