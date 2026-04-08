package com.example.demo.controller


import com.example.demo.database.dao.UserDao
import com.example.demo.model.dto.rs.UserDto
import com.example.demo.security.JwtTokenProvider
import com.example.demo.security.UserPrincipal
import com.example.demo.service.UserService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService,
    private val userDao: UserDao
) {

    data class JwtRequest(val name: String, val username: String, val password: String, val phone: String)
    data class JwtReguestLogin(val username: String, val password: String)
    data class JwtResponse(val token: String, val username: String)

    @PostMapping("/register")
    fun register(@RequestBody request: JwtRequest): ResponseEntity<JwtResponse> {
        val user = userService.createUserFromAuth(
            request.name ,request.username, request.password, request.phone
        )

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username, request.password
            )
        )

        val token = jwtTokenProvider.generateToken(authentication)
        return ResponseEntity.ok(JwtResponse(token, request.username))

    }

    @PostMapping("/login")
    fun login(@RequestBody request: JwtReguestLogin): ResponseEntity<JwtResponse> {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
            val token = jwtTokenProvider.generateToken(authentication)
            return ResponseEntity.ok(JwtResponse(token, request.username))
        }catch (e: Exception){
            throw UsernameNotFoundException(e.message)
        }
    }

    @GetMapping("/validate")
    fun validateToken(@RequestHeader("Authorization") token: String): UserDto {
        val jwtToken = token.replace("Bearer ", "")
        val authentication = jwtTokenProvider.getAuthentication(jwtToken)
        val principal = authentication.principal as UserPrincipal
        return userService.getUserById(principal.id)
    }

    @GetMapping("/user/{id}")
    fun getUserById(@PathVariable id: Long): UserDto {
        return userService.getUserById(id)
    }

    @PostMapping("/user/{id}/increment-habits")
    fun incrementHabitsCount(@PathVariable id: Long) {
        userService.incrementHabitsCount(id)
    }

    @PostMapping("/user/{id}/decrement-habits")
    fun decrementHabitsCount(@PathVariable id: Long) {
        val user = userDao.findById(id).orElseThrow()
        user.count -= 1
        userDao.save(user)
        userService.decrementHabitsCount(id)
    }

    @PostMapping("/user/{id}/add-score")
    fun addScore(@PathVariable id: Long, @RequestParam points: Int) {
        val user = userDao.findById(id).orElseThrow()
        user.totalScore += points
        userDao.save(user)
    }
}
