package com.example.demo.service.impl

import com.example.demo.database.dao.UserDao
import com.example.demo.security.UserPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import com.example.demo.database.entity.Users
import com.example.demo.model.Role
@Service
class UserDetailsServiceImpl(
    private val userDao: UserDao
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userDao.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        val authorities = listOf(
            SimpleGrantedAuthority("ROLE_${user.role.name}")
        )
        return UserPrincipal(
            userId = user.id!!,
            userEmail = user.email,
            userPassword = user.password,
            userAuthorities = authorities
        )
    }
}