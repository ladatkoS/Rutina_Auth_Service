package com.example.demo.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrincipal(
    private val userId: Long,
    private val userEmail: String,
    private val userPassword: String?,
    private val userAuthorities: Collection<GrantedAuthority>
) : UserDetails {

    val id: Long get() = userId

    override fun getAuthorities(): Collection<GrantedAuthority> = userAuthorities

    override fun getPassword(): String? = userPassword

    override fun getUsername(): String = userEmail

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}