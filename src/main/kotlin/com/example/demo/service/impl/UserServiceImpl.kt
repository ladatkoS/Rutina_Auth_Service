package com.example.demo.service.impl
import com.example.demo.database.dao.UserDao
import com.example.demo.database.entity.Users
import com.example.demo.exeptions.HabitLimitExceededException
import com.example.demo.exeptions.UserDoesntExistException
import com.example.demo.exeptions.UserExistException
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
            UserDoesntExistException("Пользователь с ID $id не найден")
        }.toDto()
    }

    @Transactional
    override fun incrementHabitsCount(userId: Long) {
        val user = userDao.findById(userId).orElseThrow { UserDoesntExistException("Пользователь не найден") }
        if (user.count < 10) {
            user.count += 1
            userDao.save(user)
        }else {
            throw HabitLimitExceededException("Нельзя добавить более 10 привычек. Удалите одну из существующих.")
        }
    }

    @Transactional
    override fun decrementHabitsCount(userId: Long) {
        val user = userDao.findById(userId).orElseThrow { UserDoesntExistException("Пользователь не найден") }
        if (user.count > 0) {
            user.count -= 1
            userDao.save(user)
        }
    }

    override fun getUserByEmail(email: String): UserDto {
        val user = userDao.findByEmail(email) ?: throw UserDoesntExistException("Пользователь с email $email не найден")
        return user.toDto()
    }


    override fun getCurrentUser(): Users {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        return userDao.findByEmail(username)
            ?: throw UserDoesntExistException("Текущий пользователь не найден")
    }

    override fun createUserFromAuth(name:String, username: String, password: String, phone: String): UserDto {
        if(userDao.existsByEmail(username)) {
            throw UserExistException("Пользователь с email $username уже зарегистрирован")
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