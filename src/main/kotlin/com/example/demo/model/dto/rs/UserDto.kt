package com.example.demo.model.dto.rs

import com.example.demo.model.Role
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val balance: Int = 0,
    val totalScore: Int = 0,
    val countOfHabits: Int = 0,
    val role: Role,
    val createdAt: LocalDateTime
)