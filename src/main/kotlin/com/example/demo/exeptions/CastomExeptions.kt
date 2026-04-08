package com.example.demo.exeptions

class UserExistException(message: String = "Пользователь с таким email уже существует") : RuntimeException(message)

class UserDoesntExistException(message: String = "Пользователь не найден") : RuntimeException(message)

class HabitLimitExceededException(message: String = "Вы не можете добавить более 10 привычек") : RuntimeException(message)

class InvalidTokenException(message: String = "Недействительный токен авторизации") : RuntimeException(message)