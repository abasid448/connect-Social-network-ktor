package com.abcoding

import com.abcoding.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.ext.Koin
import java.nio.file.Paths


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
    // Install Koin

}
