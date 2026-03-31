package com.example.demo.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*


@Component
class JwtTokenProvider {

    companion object {
        private const val JWT_SECRET = "delivery-kfd-secret-key-change-in-prod-32chars"
        private const val JWT_EXPIRATION = 86400000L
    }

    private val key = Keys.hmacShaKeyFor(JWT_SECRET.toByteArray())

    fun generateToken(authentication: Authentication): String {
        val principal = authentication.principal as UserPrincipal

        val claims: Map<String, Any> = mapOf(
            "sub" to principal.username,
            "id" to principal.id,
            "roles" to principal.authorities.map { it.authority },
            "iat" to Date(),
            "exp" to Date(System.currentTimeMillis() + JWT_EXPIRATION)
        )

        return Jwts.builder()
            .claims(claims)
            .signWith(key)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val jwtClaims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val idLong = when (val idValue = jwtClaims["id"]) {
            is Long -> idValue
            is Number -> idValue.toLong()
            is String -> idValue.toLong()
            else -> throw IllegalArgumentException("Invalid id type in token")
        }

        val rolesList = jwtClaims["roles"] as? List<*> ?: emptyList<String>()
        val authorities = rolesList.map {
            SimpleGrantedAuthority(it.toString())
        }

        val principal = UserPrincipal(
            userId = idLong,
            userEmail = jwtClaims.subject,
            userPassword = null,
            userAuthorities = authorities
        )

        return UsernamePasswordAuthenticationToken(
            principal,
            null,
            principal.authorities
        )
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        val jwtToken = token.replace("Bearer ", "")
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(jwtToken)
            .payload
        return claims.subject
    }
}