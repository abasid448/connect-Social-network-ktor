package com.abcoding.di

import com.abcoding.controller.UserControlIerImpl
import com.abcoding.controller.UserController
import com.abcoding.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserController> {
        UserControlIerImpl(get())
    }
}