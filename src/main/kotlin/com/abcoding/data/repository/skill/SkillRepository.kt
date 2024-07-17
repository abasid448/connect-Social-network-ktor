package com.abcoding.data.repository.skill

import com.abcoding.data.models.Skill

interface SkillRepository {
    suspend fun getSkills(): List<Skill>
}