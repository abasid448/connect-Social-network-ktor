package com.abcoding.di

import com.abcoding.repository.user.FakeUserRepository
import org.koin.dsl.module

internal val testModule = module {
    single { FakeUserRepository() }
}