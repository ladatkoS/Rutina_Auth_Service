package com.example.demo.database.entity

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Repository

@MappedSuperclass
abstract class AbstractEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
}