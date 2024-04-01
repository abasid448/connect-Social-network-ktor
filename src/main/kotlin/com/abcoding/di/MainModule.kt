package com.abcoding.di

import com.abcoding.data.repository.follow.FollowRepository
import com.abcoding.data.repository.follow.FollowRepositoryImpl
import com.abcoding.data.repository.user.UserRepository
import com.abcoding.data.repository.user.UserRepositoryImpl
import com.abcoding.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single <FollowRepository>{
        FollowRepositoryImpl(get())
    }
}