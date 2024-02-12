package com.abcoding.routes

import com.abcoding.controller.UserController
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
import org.koin.ktor.ext.inject


fun Route.userRoutes() {
    val userController: UserController by inject()
    route("/api/user/create") {
        post {
            val reqest = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExist = userController.getUserByEmail(reqest.email) != null
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
            userController.createUser(
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