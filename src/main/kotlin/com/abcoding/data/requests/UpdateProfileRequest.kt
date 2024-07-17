package com.abcoding.data.requests

import com.abcoding.data.responses.SkillDto

data class UpdateProfileRequest(
        val username: String,
        val bio: String,
        val gitHubUrl: String,
        val instagramUrl: String,
        val linkedInUrl: String,
        val skills: List<SkillDto>,
)
