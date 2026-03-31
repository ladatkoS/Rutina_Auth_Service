package com.example.demo.database.dao

import org.springframework.data.repository.CrudRepository
import com.example.demo.database.entity.Users
import com.example.demo.model.dto.rs.UserDto
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.Query

interface UserDao : CrudRepository<Users, Long>{
    fun findByEmail(email: String): Users?
    fun existsByEmail(email: String): Boolean
}