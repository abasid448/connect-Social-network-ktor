package com.abcoding.data.repository.post

import com.abcoding.data.models.Post
import com.abcoding.util.Constants

interface PostRepository {
    suspend fun createPostIfUserExists(post: Post): Boolean

    suspend fun deletePost(postId: String)

    suspend fun getPostsByFollows(
        userId: String,
        page: Int = 0 ,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<Post>

    suspend fun getPost(postId: String): Post?
}
