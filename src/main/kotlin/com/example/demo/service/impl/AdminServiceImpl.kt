package com.example.demo.service.impl

import com.example.demo.database.dao.UserDao
import com.example.demo.database.entity.Users
import com.example.demo.model.dto.rq.CreateUserRq
import  com.example.demo.model.dto.rs.UserDto
import com.example.demo.model.mapper.UserMapper.toDto
import com.example.demo.service.AdminService
import com.example.demo.service.UserService
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl (
    private val userDao: UserDao
): AdminService{
    override fun getUsers(): List<UserDto> {
        return userDao.findAll().map { it.toDto() }
    }

    override fun getUserById(id: Long): UserDto {
        return userDao.findById(id).orElseThrow().toDto()
    }

    override fun deleteUserById(id: Long) {
        return userDao.deleteById(id)
    }
}