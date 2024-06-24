package com.abcoding.di

import com.abcoding.data.repository.activity.ActivityRepository
import com.abcoding.data.repository.activity.ActivityRepositoryImpl
import com.abcoding.data.repository.comment.CommentRepository
import com.abcoding.data.repository.comment.CommentRepositoryImpl
import com.abcoding.data.repository.follow.FollowRepository
import com.abcoding.data.repository.follow.FollowRepositoryImpl
import com.abcoding.data.repository.likes.LikeRepository
import com.abcoding.data.repository.likes.LikeRepositoryImpl
import com.abcoding.data.repository.post.PostRepository
import com.abcoding.data.repository.post.PostRepositoryImpl
import com.abcoding.data.repository.user.UserRepository
import com.abcoding.data.repository.user.UserRepositoryImpl
import com.abcoding.service.*
import com.abcoding.util.Constants
import com.google.gson.Gson
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
    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }
    single <PostRepository>{
        PostRepositoryImpl(get())
    }
    single<LikeRepository> {
        LikeRepositoryImpl(get())
    }
    single<CommentRepository> {
        CommentRepositoryImpl(get())
    }
    single<ActivityRepository> {
        ActivityRepositoryImpl(get())
    }


    single { UserService(get(),get()) }
    single { FollowService(get()) }
    single { PostService(get()) }
    single { LikeService(get()) }
    single { CommentService(get()) }
    single { ActivityService(get(), get(), get()) }
    single { Gson() }
}