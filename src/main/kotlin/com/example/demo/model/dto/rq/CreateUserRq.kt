package com.example.demo.model.dto.rq

import java.time.LocalDateTime

data class CreateUserRq(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
)