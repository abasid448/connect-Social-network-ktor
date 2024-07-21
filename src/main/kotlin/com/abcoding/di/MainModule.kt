package com.abcoding.di

import com.abcoding.data.repository.activity.ActivityRepository
import com.abcoding.data.repository.activity.ActivityRepositoryImpl
import com.abcoding.data.repository.chat.ChatRepository
import com.abcoding.data.repository.chat.ChatRepositoryImpl
import com.abcoding.data.repository.comment.CommentRepository
import com.abcoding.data.repository.comment.CommentRepositoryImpl
import com.abcoding.data.repository.follow.FollowRepository
import com.abcoding.data.repository.follow.FollowRepositoryImpl
import com.abcoding.data.repository.likes.LikeRepository
import com.abcoding.data.repository.likes.LikeRepositoryImpl
import com.abcoding.data.repository.post.PostRepository
import com.abcoding.data.repository.post.PostRepositoryImpl
import com.abcoding.data.repository.skill.SkillRepository
import com.abcoding.data.repository.skill.SkillRepositoryImpl
import com.abcoding.data.repository.user.UserRepository
import com.abcoding.data.repository.user.UserRepositoryImpl
import com.abcoding.service.*
import com.abcoding.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    single<SkillRepository> {
        SkillRepositoryImpl(get())
    }
    single<ChatRepository> {
        ChatRepositoryImpl(get())
    }



    single { UserService(get(),get()) }
    single { FollowService(get()) }
    single { PostService(get()) }
    single { LikeService(get(),get(),get()) }
    single { CommentService(get(), get()) }
    single { ActivityService(get(), get(), get()) }
    single { SkillService(get()) }
    single { ChatService(get()) }
    single { Gson() }
}