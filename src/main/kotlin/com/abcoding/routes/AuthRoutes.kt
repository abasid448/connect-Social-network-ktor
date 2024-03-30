package com.abcoding.routes

import com.abcoding.data.repository.user.UserRepository
import com.abcoding.data.models.User
import com.abcoding.data.requests.CreateAccountRequest
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.util.ApiResponseMessages.FIELDS_BLANK
import com.abcoding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.createUser(userRepository: UserRepository) {
    route("/api/user/create") {
        post {

//            val reqest = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
//                call.respond(HttpStatusCode.BadRequest)
//                return@post
//            }

            val reqest = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userExist = userRepository.getUserByEmail(reqest.email) != null
            if (userExist) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXISTS
                    )
                )
                return@post
            }
            if (reqest.username.isBlank() || reqest.email.isBlank() || reqest.password.isBlank()) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = FIELDS_BLANK
                    )
                )
                return@post
            }
            userRepository.createUser(
                User(
                    email = reqest.email,
                    username = reqest.username,
                    password = reqest.password,
                    profileImageUrl = "",
                    bio =  "",
                    bannerUrl = "",
                    gitHubUrl = null,
                    instagramUrl = null,
                    linkedInUrl = null
                )
            )
            call.respond(
                BasicApiResponse(
                    successful = true,
                )
            )
        }
    }
}