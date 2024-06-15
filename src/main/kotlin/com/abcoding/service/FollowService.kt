package com.abcoding.service

import com.abcoding.data.repository.follow.FollowRepository
import com.abcoding.data.requests.CreateAccountRequest
import com.abcoding.data.requests.FollowUpdateRequest

class FollowService(
        private val followRepository: FollowRepository
) {

    suspend fun followUserIfExists(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.followUserIfExists(
                followingUserId,
                request.followedUserId
        )
    }

    suspend fun unfollowUserIfExists(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.unfollowUserIfExists(
                followingUserId,
                request.followedUserId
        )
    }
}