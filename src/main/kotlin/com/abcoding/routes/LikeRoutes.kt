package com.abcoding.routes

import com.abcoding.data.requests.LikeUpdateRequest
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.service.LikeService
import com.abcoding.service.UserService
import com.abcoding.util.ApiResponseMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.likeParent(
        likeService: LikeService,
        userService: UserService
) {
    authenticate {
        post("/api/like") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(
                        HttpStatusCode.BadRequest
                )
                return@post
            }
            ifEmailBelongsToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
            ) {
                val likeSuccessful = likeService.likeParent(request.userId, request.parentId)
                if (likeSuccessful) {
                    call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                    successful = true
                            )
                    )
                } else {
                    call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.USER_NOT_FOUND
                            )
                    )
                }
            }
        }
    }
}
fun Route.unlikeParent(
        likeService: LikeService,
        userService: UserService
) {
    authenticate {
        delete("/api/unlike") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            ifEmailBelongsToUser(
                    userId = request.userId,
                    validateEmail = userService::doesEmailBelongToUserId
            ) {
                val unlikeSuccessful = likeService.unlikeParent(request.userId, request.parentId)
                if(unlikeSuccessful) {
                    call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                    successful = true
                            )
                    )
                } else {
                    call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                    successful = false,
                                    message = ApiResponseMessages.USER_NOT_FOUND
                            )
                    )
                }
            }
        }
    }
}