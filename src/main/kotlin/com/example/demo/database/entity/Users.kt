package com.example.demo.database.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import com.example.demo.model.Role
import com.example.demo.model.Role.USER

@Entity
@Table(name = "users")
data class Users(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "email", nullable = false, unique = true) // Добавьте unique
    var email: String,

    @Column(name = "phone", nullable = false)
    var phone: String,

    @Column(name = "balance")
    var balance: Int = 0,

    @Column(name = "total_score")
    var totalScore: Int = 0,

    @Column(name = "count_of_habits")
    var count: Int = 0,

    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "role")
    var role: Role = USER,
) : AbstractEntity() {
}
