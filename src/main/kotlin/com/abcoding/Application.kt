package com.abcoding

import com.abcoding.di.mainModule
import com.abcoding.plugins.*
import io.ktor.server.application.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}



@Suppress("unused")
fun Application.module() {
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
    startKoin {
        modules(mainModule)
    }

}
