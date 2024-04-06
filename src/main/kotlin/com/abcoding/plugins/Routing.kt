package com.abcoding.plugins


import com.abcoding.data.repository.follow.FollowRepository
import com.abcoding.data.repository.post.PostRepository
import com.abcoding.data.repository.user.UserRepository
import com.abcoding.routes.*
import com.abcoding.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val userService: UserService by inject()
    val followRepository: FollowRepository by inject()
    val postRepository: PostRepository by inject()
    routing {
        // User routes
        createUserRoute(userService)
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)

        // Post routes
        createPostRoute(postRepository)
    }
}

