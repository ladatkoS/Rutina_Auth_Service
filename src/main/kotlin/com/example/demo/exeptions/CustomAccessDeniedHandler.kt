package com.example.demo.exeptions

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.ObjectMapper

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = mapOf(
            "timestamp" to java.time.LocalDateTime.now().toString(),
            "status" to 403,
            "error" to "Forbidden",
            "message" to "Доступ запрещён. Требуются права администратора.",
            "path" to request.requestURI
        )

        val objectMapper = ObjectMapper()
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}