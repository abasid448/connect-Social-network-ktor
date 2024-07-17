package com.abcoding.service

import com.abcoding.data.models.Activity
import com.abcoding.data.repository.activity.ActivityRepository
import com.abcoding.data.repository.comment.CommentRepository
import com.abcoding.data.repository.post.PostRepository
import com.abcoding.data.responses.ActivityResponse
import com.abcoding.data.util.ActivityType
import com.abcoding.data.util.ParentType
import com.abcoding.util.Constants

class ActivityService(
        private val activityRepository: ActivityRepository,
        private val postRepository: PostRepository,
        private val commentRepository: CommentRepository
) {
    suspend fun getActivitiesForUser(
            userId: String,
            page: Int = 0,
            pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<ActivityResponse> {
        return activityRepository.getActivitiesForUser(userId, page, pageSize)

    }

    suspend fun addCommentActivity(
            byUserId: String,
            postId: String
    ): Boolean {
        val userIdOfPost = postRepository.getPost(postId)?.userId ?: return false
        if (byUserId == userIdOfPost) {
            return false
        }
        activityRepository.createActivity(
                Activity(
                        timestamp = System.currentTimeMillis(),
                        byUserId = byUserId,
                        toUserId = userIdOfPost,
                        type = ActivityType.CommentedOnPost.type,
                        parentId = postId
                )
        )
        return true
    }
    suspend fun addLikeActivity(
            byUserId: String,
            parentType: ParentType,
            parentId: String
    ):Boolean{
            val toUserId = when(parentType){
                is ParentType.Post -> {
                    postRepository.getPost(parentId)?.userId
                }
                is ParentType.Comment -> {
                    commentRepository.getComment(parentId)?.userId
                }
                is ParentType.None -> return false
            } ?: return false
        if (byUserId == toUserId) {
            return false
        }
        activityRepository.createActivity(
                Activity(
                        timestamp = System.currentTimeMillis(),
                        byUserId = byUserId,
                        toUserId = toUserId,
                        type = when(parentType){
                            is ParentType.Post -> ActivityType.LikedPost.type
                            is ParentType.Comment -> ActivityType.LikedComment.type
                            else -> ActivityType.LikedPost.type
                        },
                        parentId = parentId
                )
        )
        return true
    }
    suspend fun createActivity(activity: Activity) {
        activityRepository.createActivity(activity)
    }

    suspend fun deleteActivity(activityId: String): Boolean {
        return activityRepository.deleteActivity(activityId)
    }
}