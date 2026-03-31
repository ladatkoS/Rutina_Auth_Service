package com.example.demo.service

import com.example.demo.database.entity.Users
import com.example.demo.model.dto.rq.CreateUserRq
import  com.example.demo.model.dto.rs.UserDto
import java.time.LocalDateTime

interface UserService {
    fun getCurrentUser(): Users
    fun getUserById(id: Long): UserDto
    fun getUserByEmail(email: String): UserDto
    fun incrementHabitsCount(userId: Long)
    fun decrementHabitsCount(userId: Long)
    fun createUserFromAuth(name: String,username: String, password: String, phone: String): UserDto
}