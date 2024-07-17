package com.abcoding.routes

import com.abcoding.data.models.User
import com.abcoding.data.requests.CreateAccountRequest
import com.abcoding.data.requests.LoginRequest
import com.abcoding.data.requests.UpdateProfileRequest
import com.abcoding.data.responses.AuthResponse
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.data.responses.UserResponseItem
import com.abcoding.service.PostService
import com.abcoding.service.UserService
import com.abcoding.util.ApiResponseMessages
import com.abcoding.util.ApiResponseMessages.FIELDS_BLANK
import com.abcoding.util.ApiResponseMessages.INVALID_CREDENTIALS
import com.abcoding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import com.abcoding.util.Constants
import com.abcoding.util.Constants.BANNER_IMAGE_PATH
import com.abcoding.util.Constants.BASE_URL
import com.abcoding.util.Constants.PROFILE_PICTURE_PATH
import com.abcoding.util.QueryParams
import com.abcoding.util.save
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

fun Route.searchUser(userService: UserService) {
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if (query == null || query.isBlank()) {
                call.respond(
                        HttpStatusCode.OK,
                        listOf<UserResponseItem>()
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
                        HttpStatusCode.OK, BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                )
                )
                return@get
            }
            call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(successful = true,
                        data = profileResponse
                    )
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
            var profilePictureFileName: String? = null
            var bannerImageFileName: String? = null
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
                        if (partData.name == "profile_picture") {
                            profilePictureFileName = partData.save(PROFILE_PICTURE_PATH)
                        } else if (partData.name == "banner_image") {
                            bannerImageFileName = partData.save(BANNER_IMAGE_PATH)
                        }
                    }
                    else -> Unit // handle any other types if needed
                }
                partData.dispose()
            }

            val profilePictureUrl = profilePictureFileName?.let { "${BASE_URL}profile_pictures/$it" }
            val bannerImageUrl = bannerImageFileName?.let { "${BASE_URL}banner_images/$it" }

            updateProfileRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                        userId = call.userId,
                        profileImageUrl = profilePictureUrl,
                        bannerUrl = bannerImageUrl,
                        updateProfileRequest = request
                )
                if (updateAcknowledged) {
                    call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(
                                    successful = true
                            )
                    )
                } else {
                    profilePictureFileName?.let { File("${PROFILE_PICTURE_PATH}/$it").delete() }
                    bannerImageFileName?.let { File("${BANNER_IMAGE_PATH}/$it").delete() }
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}