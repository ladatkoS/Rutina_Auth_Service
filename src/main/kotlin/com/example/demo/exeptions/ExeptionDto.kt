package com.example.demo.exeptions

data class ApiError(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: String = java.time.LocalDateTime.now().toString()
)
