package com.example.demo.service

import com.example.demo.database.entity.Users
import com.example.demo.model.dto.rq.CreateUserRq
import  com.example.demo.model.dto.rs.UserDto

interface AdminService {
    fun getUsers(): List<UserDto>
    fun getUserById(id: Long): UserDto
    fun deleteUserById(id: Long)
}