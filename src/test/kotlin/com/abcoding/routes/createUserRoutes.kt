package com.abcoding.routes

import com.abcoding.data.models.User
import com.abcoding.data.repository.user.FakeUserRepository
import com.abcoding.data.requests.CreateAccountRequest
import com.abcoding.data.responses.BasicApiResponse
import com.abcoding.di.testModule
import com.abcoding.plugins.configureSerialization
import com.abcoding.util.ApiResponseMessages
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest


internal class createUserRoutes : KoinTest {

    private val userRepository by inject<FakeUserRepository>() // Specify the qualifier for the test

    private val gson = Gson()

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(testModule) // Use the test module here
        }
    }

    @AfterTest
    fun tearDown(){
        stopKoin()
    }

    @Test
    fun `Create user, no body attached, responds with BadRequest`() {

        testApplication {
            application {
                install(Routing){
                    createUser(userRepository)
                }
            }
            val request= client.post("/api/user/create")
                assertThat(request.status).isEqualTo(HttpStatusCode.BadRequest)
            }
    }

    @Test
    fun `Create User, User already exists, responds with unsuccessful`() = runBlocking {
        val user = User(
            email = "test@test.com",
            username = "test",
            password = "test",
            profileImageUrl = "",
            bannerUrl = "",
            bio = "",
            gitHubUrl = null,
            instagramUrl = null,
            linkedInUrl = null,
        )
        userRepository.createUser(user)
        testApplication {
            application {
                configureSerialization()
                install(Routing) {
                    createUser(userRepository)
                }
            }
            val request = client.post("/api/user/create")
            {
                header("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "asdf",
                    password = "asdf"
                )
                setBody(gson.toJson(request))
            }
                val response = gson.fromJson(
                    request.bodyAsText(), BasicApiResponse::class.java
                )
                assertThat(response.successful).isFalse()
                assertThat(response.message).isEqualTo(ApiResponseMessages.USER_ALREADY_EXISTS)
        }
    }
    @Test
    fun `Create user, email is empty, responds with unsuccessful`()  {
        testApplication {
            application {
                configureSerialization()
                install(Routing) {
                    createUser(userRepository)
                }
            }
            val request = client.post("/api/user/create")
            {
                header("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "",
                    username = "",
                    password = ""
                )
                setBody(gson.toJson(request))
            }
                val response = gson.fromJson(
                    request.bodyAsText(), BasicApiResponse::class.java
                )
                assertThat(response.successful).isFalse()
                assertThat(response.message).isEqualTo(ApiResponseMessages.FIELDS_BLANK)
        }
    }

    @Test
    fun  `Create user, valid data, responds with successful`(){
        testApplication {
            application {
                install(Routing){
                    createUser(userRepository  )
                }
            }
            val request = client.post(
                "/api/user/create"
            )
            {
                header("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "test"
                )
                setBody(gson.toJson(request))
            }
            val response = gson.fromJson(
            request.bodyAsText(), BasicApiResponse::class.java
        )
            assertThat(response.successful).isTrue()

            runBlocking {
                val isUserInDb = userRepository.getUserByEmail("test@test.com") != null
                assertThat(isUserInDb).isTrue()
            }

        }
    }
}
