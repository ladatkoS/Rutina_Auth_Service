package com.example.demo.controller
import com.example.demo.service.AdminService
import com.example.demo.service.UserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/admin")
class AdminController (
    private val adminService: AdminService,
){

    @GetMapping("/allusers")

    fun getUsers() = adminService.getUsers()

    @GetMapping("info/{id}")
    fun getUser(@PathVariable("id") id: Long) = adminService.getUserById(id)

    @DeleteMapping("delete/{id}")
    fun deleteUser(@PathVariable("id") id: Long) = adminService.deleteUserById(id)
}