package com.abcoding.service

import com.abcoding.data.models.Skill
import com.abcoding.data.repository.skill.SkillRepository

class SkillService(
        private val repository: SkillRepository
) {
    suspend fun getSkills(): List<Skill>{
        return repository.getSkills()
    }

}