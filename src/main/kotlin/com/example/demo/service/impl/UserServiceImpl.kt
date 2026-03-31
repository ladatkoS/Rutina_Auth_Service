package com.example.demo.service.impl
import com.example.demo.database.dao.UserDao
import com.example.demo.database.entity.Users
import com.example.demo.model.Role
import com.example.demo.model.dto.rs.UserDto
import com.example.demo.model.mapper.UserMapper.toDto
import com.example.demo.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class UserServiceImpl(
    private val userDao: UserDao,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun getUserById(id: Long): UserDto {
        return userDao.findById(id).orElseThrow {
            RuntimeException("User not found")
        }.toDto()
    }

    @Transactional
    override fun incrementHabitsCount(userId: Long) {
        val user = userDao.findById(userId).orElseThrow { RuntimeException("User not found") }
        if (user.count < 10) {
            user.count += 1
            userDao.save(user)
        }
    }

    @Transactional
    override fun decrementHabitsCount(userId: Long) {
        val user = userDao.findById(userId).orElseThrow { RuntimeException("User not found") }
        if (user.count > 0) {
            user.count -= 1
            userDao.save(user)
        }
    }

    override fun getUserByEmail(email: String): UserDto {
        val user = userDao.findByEmail(email) ?: throw RuntimeException("User not found")
        return user.toDto()
    }


    override fun getCurrentUser(): Users {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        return userDao.findByEmail(username)
            ?: throw RuntimeException("User not found")
    }

    override fun createUserFromAuth(name:String, username: String, password: String, phone: String): UserDto {
        if(userDao.existsByEmail(username)) {
            throw RuntimeException("User already exists")
        }

        val user = Users(
            name = name,
            email = username,
            phone = phone,
            password = passwordEncoder.encode(password),
            role = Role.USER
        )

        return userDao.save(user).toDto()
    }
}