package com.example.demo.service.impl

import com.example.demo.database.dao.UserDao
import com.example.demo.database.entity.Users
import  com.example.demo.model.dto.rs.UserDto
import com.example.demo.model.mapper.UserMapper.toDto
import com.example.demo.service.AdminService
import org.springframework.stereotype.Service
import com.example.demo.exeptions.UserDoesntExistException
import com.example.demo.model.Role
import org.springframework.security.core.context.SecurityContextHolder


@Service
class AdminServiceImpl(
    private val userDao: UserDao
) : AdminService {

    override fun getUsers(): List<UserDto> {
        return userDao.findAll().map { it.toDto() }
    }

    override fun getUserById(id: Long): UserDto {
        return userDao.findById(id).orElseThrow {
            UserDoesntExistException("Пользователя с ID $id не существует")
        }.toDto()
    }

    override fun deleteUserById(id: Long) {
        if (!userDao.existsById(id)) {
            throw UserDoesntExistException("Невозможно удалить: пользователь с ID $id не найден")
        }
        userDao.deleteById(id)
    }
}