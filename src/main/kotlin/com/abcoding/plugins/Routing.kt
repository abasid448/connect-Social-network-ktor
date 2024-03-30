package com.abcoding.plugins


import com.abcoding.data.repository.user.UserRepository
import com.abcoding.routes.createUser
import com.abcoding.routes.loginUser
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject



fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    routing {
        createUser(userRepository)  // Assuming createUser handles user creation
        loginUser(userRepository)
    }
}

