package com.abcoding

import com.abcoding.di.mainModule
import com.abcoding.plugins.*
import io.ktor.server.application.*
import org.koin.core.context.startKoin
import java.nio.file.Paths

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
@Suppress("unused")
fun Application.module() {

    configureSecurity()
    configureSockets()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    startKoin {
        modules(mainModule)
    }
}
