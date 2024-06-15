package com.abcoding.service

import com.abcoding.data.models.Comment
import com.abcoding.data.repository.comment.CommentRepository
import com.abcoding.data.requests.CreateCommentRequest
import com.abcoding.util.Constants

class CommentService(
        private val repository: CommentRepository
) {

    suspend fun createComment(createCommentRequest: CreateCommentRequest): ValidationEvent {
        createCommentRequest.apply {
            if (comment.isBlank() || userId.isBlank() || postId.isBlank()) {
                return ValidationEvent.ErrorFieldEmpty
            }
            if (comment.length > Constants.MAX_COMMENT_LENGTH) {
                return ValidationEvent.ErrorCommentTooLong
            }
        }

        repository.createComment(
                Comment(
                        comment = createCommentRequest.comment,
                        userId = createCommentRequest.userId,
                        postId = createCommentRequest.postId,
                        timeStamp = System.currentTimeMillis()
                )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment> {
        return repository.getCommentsForPost(postId)
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()

        object ErrorCommentTooLong : ValidationEvent()

        object Success : ValidationEvent()
    }
}