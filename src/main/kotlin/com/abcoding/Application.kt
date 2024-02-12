package com.abcoding

import com.abcoding.plugins.*
import io.ktor.server.application.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val mainModule  = module {
    single {
        "hello world"
    }
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
    val  helloWorld: String by inject()
    println(helloWorld)

}
