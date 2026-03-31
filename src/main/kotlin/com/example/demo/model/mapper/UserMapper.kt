package com.example.demo.model.mapper

import com.example.demo.database.entity.Users
import com.example.demo.model.dto.rq.CreateUserRq
import com.example.demo.model.dto.rs.UserDto
import org.springframework.security.crypto.password.PasswordEncoder

object UserMapper {

    fun CreateUserRq.toEntity(passwordEncoder: PasswordEncoder): Users {
        return Users(
            name = this.name,
            email = this.email,
            phone = this.phone,
            password = passwordEncoder.encode(this.password),
        )
    }

    fun Users.toDto(): UserDto {
        return UserDto(
            id = this.id!!,
            name = this.name,
            email = this.email,
            phone = this.phone,
            balance = this.balance,
            totalScore = this.totalScore,
            countOfHabits = this.count,
            createdAt = this.createdAt
        )
    }
}