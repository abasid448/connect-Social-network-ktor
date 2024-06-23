package com.abcoding.routes

import com.abcoding.data.requests.LikeUpdateRequest
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.data.util.ParentType
import com.abcoding.service.ActivityService
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
        activityService: ActivityService
) {
    authenticate {
        post("/api/like") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userId = call.userId
            val likeSuccessful = likeService.likeParent(userId, request.parentId, request.parentType)
            if(likeSuccessful) {
                activityService.addLikeActivity(
                        byUserId = userId,
                        parentType = ParentType.fromType(request.parentType),
                        parentId = request.parentId
                )
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
fun Route.unlikeParent(
        likeService: LikeService,
) {
    authenticate {
        delete("/api/unlike") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val unlikeSuccessful = likeService.unlikeParent(call.userId, request.parentId)
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

