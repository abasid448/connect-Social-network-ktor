package com.abcoding.plugins


import com.abcoding.data.repository.follow.FollowRepository
import com.abcoding.data.repository.user.UserRepository
import com.abcoding.data.util.ActivityType
import com.abcoding.routes.createUser
import com.abcoding.routes.followUser
import com.abcoding.routes.loginUser
import com.abcoding.routes.unfollowUser
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject



fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    routing {
        createUser(userRepository)  // Assuming createUser handles user creation
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)
    }
}

