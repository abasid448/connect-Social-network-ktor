val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project
val kmongo_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
}

group = "com.abcoding"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

}


repositories {
    mavenCentral()
}


dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-gson-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // KMongo
    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")
    // Koin core features
    implementation("io.insert-koin:koin-core:$koin_version")
    testImplementation("io.insert-koin:koin-test:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // Koin
    testImplementation("io.insert-koin:koin-test:$koin_version")

    // Test dependencies

    implementation("io.ktor:ktor-server-host-common:$ktor_version")

    // Gson
    testImplementation("com.google.code.gson:gson:2.8.8")
    // Koin
    testImplementation("io.insert-koin:koin-test")
    // Ktor Test
    testImplementation("io.ktor:ktor-server-tests")
    // Kotlin Test
    testImplementation ("org.jetbrains.kotlin:kotlin-test")
    // Truth
    testImplementation ("com.google.truth:truth:1.4.2")



}
