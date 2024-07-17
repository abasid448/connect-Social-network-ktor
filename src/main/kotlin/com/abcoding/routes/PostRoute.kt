package com.abcoding.routes

import com.abcoding.data.requests.CreatePostRequest
import com.abcoding.data.requests.DeletePostRequest
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.service.CommentService
import com.abcoding.service.LikeService
import com.abcoding.service.PostService
import com.abcoding.util.ApiResponseMessages
import com.abcoding.util.Constants
import com.abcoding.util.QueryParams
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

fun Route.createPost(
        postService: PostService,
) {
    val gson by inject<Gson>()
    authenticate {
        post("/api/post/create") {
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "post_data") {
                            createPostRequest = gson.fromJson(
                                    partData.value,
                                    CreatePostRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        val fileBytes = partData.streamProvider().readBytes()
                        val file = File(Constants.POST_PICTURE_PATH, partData.originalFileName!!)
                        file.writeBytes(fileBytes)
                        fileName = file.name
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> TODO()
                }
            }

            val postPictureUrl = "${Constants.BASE_URL}post_pictures/$fileName"

            createPostRequest?.let { request ->
                val createPostAcknowledged = postService.createPost(
                        request = request,
                        userId = call.userId,
                        imageUrl = postPictureUrl
                )
                if (createPostAcknowledged) {
                    call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse<Unit>(
                                    successful = true
                            )
                    )
                } else {
                    File("${Constants.POST_PICTURE_PATH}/$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
        }
    }
}
fun Route.getPostsForProfile(postService: PostService){
    authenticate {
        get("/api/user/posts") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForProfile(
                userId = userId ?: call.userId,
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
fun Route.getPostsForFollows(
        postService: PostService,
) {
    authenticate {
        get("/api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                    call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForFollows(call.userId, page, pageSize)
            call.respond(
                    HttpStatusCode.OK,
                    posts
            )
        }
    }
}

fun Route.deletePost(
        postService: PostService,
        likeService: LikeService,
        commentService: CommentService
) {
    authenticate {
        delete("/api/post/delete") {
            val request = call.receiveNullable<DeletePostRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val post = postService.getPost(request.postId)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            if (post.userId == call.userId) {
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)
                commentService.deleteCommentsForPost(request.postId)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
fun Route.getPostDetails(postService: PostService) {
    authenticate {
        get("/api/post/details") {
            val postId = call.parameters["postId"] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val post = postService.getPost(postId) ?: kotlin.run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true,
                    data = post
                )
            )
        }
    }
}