package com.abcoding.plugins


import com.abcoding.data.repository.follow.FollowRepository
import com.abcoding.data.repository.post.PostRepository
import com.abcoding.data.repository.user.UserRepository
import com.abcoding.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject



fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()
    val postRepository: PostRepository by inject()
    routing {
        createUser(userRepository)  // Assuming createUser handles user creation
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)

        // Post Routes

        createPostRoute(postRepository)
    }
}

