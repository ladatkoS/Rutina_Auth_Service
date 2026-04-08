package com.example.demo.exeptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.validation.FieldError
import org.slf4j.LoggerFactory
import jakarta.persistence.EntityNotFoundException
import com.example.demo.exeptions.ApiError

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(UserExistException::class)
    fun handleUserExistException(e: UserExistException): ResponseEntity<ApiError> {
        logger.warn("Ошибка регистрации: ${e.message}")
        val apiError = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Ошибка регистрации",
            message = e.message ?: "Пользователь с таким email уже существует"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError)
    }

    @ExceptionHandler(UserDoesntExistException::class, EntityNotFoundException::class)
    fun handleUserDoesntExistException(e: RuntimeException): ResponseEntity<ApiError> {
        logger.warn("Пользователь не найден: ${e.message}")
        val apiError = ApiError(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Пользователь не найден",
            message = e.message ?: "Пользователь с указанными данными не существует"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError)
    }


    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(e: UsernameNotFoundException): ResponseEntity<ApiError> {
        logger.warn("Пользователь не найден: ${e.message}")
        val apiError = ApiError(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Ошибка входа",
            message = "Неверный email или пароль"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError)
    }


    @ExceptionHandler(HabitLimitExceededException::class)
    fun handleHabitLimitExceededException(e: HabitLimitExceededException): ResponseEntity<ApiError> {
        logger.warn("Ошибка добавления привычки: ${e.message}")
        val apiError = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Лимит привычек",
            message = e.message ?: "Нельзя добавить более 10 привычек"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(e: InvalidTokenException): ResponseEntity<ApiError> {
        logger.warn("Недействительный токен: ${e.message}")
        val apiError = ApiError(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Ошибка авторизации",
            message = e.message ?: "Сессия истекла, войдите заново"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val errors = ex.bindingResult.allErrors.joinToString(", ") { error ->
            when (error) {
                is FieldError -> "${error.field}: ${error.defaultMessage}"
                else -> error.defaultMessage ?: "Неверное значение"
            }
        }

        val apiError = ApiError(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Ошибка валидации",
            message = errors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ApiError> {
        logger.error("Непредвиденная ошибка сервера", e)
        val apiError = ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Внутренняя ошибка сервера",
            message = "Произошла непредвиденная ошибка. Попробуйте позже."
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError)
    }
}
