package com.abcoding.routes

import com.abcoding.data.models.User
import com.abcoding.data.repository.user.UserRepository
import com.abcoding.data.requests.CreateAccountRequest
import com.abcoding.data.requests.LoginRequest
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.util.ApiResponseMessages.FIELDS_BLANK
import com.abcoding.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.abcoding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.createUser(userRepository: UserRepository) {
    route("/api/user/create") {
        post {
            val request = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userExist = userRepository.getUserByEmail(request.email) != null
            if (userExist) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXISTS
                    )
                )
                return@post
            }
            if (request.username.isBlank() || request.email.isBlank() || request.password.isBlank()) {
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
                    email = request.email,
                    username = request.username,
                    password = request.password,
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
fun Route.loginUser(userRepository: UserRepository){
    post("/api/user/login"){
//
        val request = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (request.email.isBlank() || request.password.isBlank()){
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val isCorrectPassword = userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
        if (isCorrectPassword){
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true
                )
            )
        }else{
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = false,
                    message = INVALID_CREDENTIALS
                )
            )

        }
    }
}