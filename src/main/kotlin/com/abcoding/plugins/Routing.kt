package com.abcoding.plugins


import com.abcoding.routes.*
import com.abcoding.service.FollowService
import com.abcoding.service.LikeService
import com.abcoding.service.PostService
import com.abcoding.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val userService: UserService by inject()

    val followService: FollowService by inject()

    val postService: PostService by inject()

    val likeService: LikeService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()


    routing {
        // User routes
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        // Following routes
        followUser(followService)
        unfollowUser(followService)

        // Post routes
        createPost(postService,userService)
        getPostsForFollows(postService, userService)

        //like route
        likeParent(likeService,userService)
        unlikeParent(likeService, userService)
    }
}

