package com.abcoding.data.repository.skill

import com.abcoding.data.models.Skill
import org.litote.kmongo.coroutine.CoroutineDatabase

class SkillRepositoryImpl(
        private val db: CoroutineDatabase
):SkillRepository {
    private val skills = db.getCollection<Skill>()
    override suspend fun getSkills(): List<Skill> {
        return skills.find().toList()
    }
}