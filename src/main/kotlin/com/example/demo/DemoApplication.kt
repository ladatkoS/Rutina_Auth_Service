package com.example.demo
import com.example.demo.database.dao.UserDao
import com.example.demo.database.entity.Users
import com.example.demo.model.Role
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.password.PasswordEncoder


@SpringBootApplication
@EnableScheduling
class DemoApplication {

    @Bean
    fun initAdmin(userDao: UserDao, passwordEncoder: PasswordEncoder): CommandLineRunner {
        return CommandLineRunner {
            if (!userDao.existsByEmail("admin@example.com")) {
                val admin = Users(
                    name = "System Administrator",
                    email = "admin@example.com",
                    phone = "+79991112233",
                    password = passwordEncoder.encode("Admin123!"),
                    role = Role.ADMIN
                )
                userDao.save(admin)
                println("✅ Admin user created: admin@example.com")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
