package com.abcoding.routes

import com.abcoding.data.models.Post
import com.abcoding.data.repository.post.PostRepository
import com.abcoding.data.requests.CreatePostRequest
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPostRoute(postRepository: PostRepository) {
    post("/api/post/create") {
        val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val didUserExists = postRepository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = request.userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
        if (!didUserExists){
            call.respond(HttpStatusCode.OK,
            BasicApiResponse(
                successful = false,
                message = ApiResponseMessages.USER_NOT_FOUND
            ))
        }
        else{
            call.respond(HttpStatusCode.OK,
            BasicApiResponse(
                successful = true
            ))
        }
    }
}