package com.abcoding.routes

import com.abcoding.data.models.User
import com.abcoding.data.requests.CreateAccountRequest
import com.abcoding.data.requests.LoginRequest
import com.abcoding.data.requests.UpdateProfileRequest
import com.abcoding.data.responses.AuthResponse
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.service.PostService
import com.abcoding.service.UserService
import com.abcoding.util.ApiResponseMessages
import com.abcoding.util.ApiResponseMessages.FIELDS_BLANK
import com.abcoding.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.abcoding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import com.abcoding.util.Constants
import com.abcoding.util.Constants.BASE_URL
import com.abcoding.util.Constants.PROFILE_PICTURE_PATH
import com.abcoding.util.QueryParams
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*


fun Route.createUser(userService: UserService) {
    post("/api/user/create") {
        val request = call.receiveNullable<CreateAccountRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (userService.doesUserWithEmailExist(request.email)) {
            call.respond(
                    BasicApiResponse(
                            successful = false,
                            message = USER_ALREADY_EXISTS
                    )
            )
            return@post
        }
        when (userService.validateCreateAccountRequest(request)) {
            is UserService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                        BasicApiResponse(
                                successful = false,
                                message = FIELDS_BLANK
                        )
                )
            }

            is UserService.ValidationEvent.Success -> {
                userService.createUser(request)
                call.respond(
                        BasicApiResponse(successful = true)
                )
            }
        }
    }
}

fun Route.loginUser(
        userService: UserService,
        jwtIssuer: String,
        jwtAudience: String,
        jwtSecret: String
) {
    post("/api/user/login") {
        val request = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userService.getUserByEmail(request.email) ?: kotlin.run {
            call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                            successful = false,
                            message = INVALID_CREDENTIALS
                    )
            )
            return@post
        }
        val isCorrectPassword = userService.isValidPassword(
                enteredPassword = request.password,
                actualPassword = user.password
        )
        if (isCorrectPassword) {
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                    .withClaim("userId", user.id)
                    .withIssuer(jwtIssuer)
                    .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                    .withAudience(jwtAudience)
                    .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                    HttpStatusCode.OK,
                    AuthResponse(token = token)
            )
        } else {
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

fun Route.searchUser(userService: UserService) {
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if (query == null || query.isBlank()) {
                call.respond(
                        HttpStatusCode.OK,
                        listOf<User>()
                )
                return@get
            }
            val searchResults = userService.searchForUsers(query, call.userId)
            call.respond(
                    HttpStatusCode.OK,
                    searchResults
            )
        }
    }
}

fun Route.getPostsForProfile(
        postService: PostService
) {
    authenticate {
        get("/api/user/posts") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull()
                    ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForProfile(
                    userId = call.userId,
                    page = page,
                    pageSize = pageSize
            )
            call.respond(
                    HttpStatusCode.OK,
                    posts
            )
        }
    }
}

fun Route.getUserProfile(userService: UserService) {
    authenticate {
        get("/api/user/profile") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            if (userId == null || userId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val profileResponse = userService.getUserProfile(userId, call.userId)
            if (profileResponse == null) {
                call.respond(
                        HttpStatusCode.OK, BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                )
                )
                return@get
            }
            call.respond(
                    HttpStatusCode.OK,
                    profileResponse
            )
        }
    }
}

fun Route.updateUserProfile(userService: UserService) {
    val gson: Gson by inject()
    authenticate {
        put("/api/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest: UpdateProfileRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest = gson.fromJson(
                                    partData.value,
                                    UpdateProfileRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        val fileBytes = partData.streamProvider().readBytes()
                        val fileExtension = partData.originalFileName?.takeLastWhile { it != '.' }
                        fileName = UUID.randomUUID().toString() + "." + fileExtension
                        File("$PROFILE_PICTURE_PATH$fileName").writeBytes(fileBytes)
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> TODO()
                }
            }

            val profilePictureUrl = "${BASE_URL}profile_pictures/$fileName"

            updateProfileRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                        userId = call.userId,
                        profileImageUrl = profilePictureUrl,
                        updateProfileRequest = request
                )
                if (updateAcknowledged) {
                    call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                    successful = true
                            )
                    )
                } else {
                    File("${PROFILE_PICTURE_PATH}/$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}