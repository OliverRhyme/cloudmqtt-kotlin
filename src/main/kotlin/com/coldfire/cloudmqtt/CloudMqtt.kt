package com.coldfire.cloudmqtt

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BasicAuthCredentials
import io.ktor.client.features.auth.providers.basic
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CloudMqtt internal constructor(
    private val client: HttpClient
) {

    companion object {
        private const val BASE_URL = "https://api.cloudmqtt.com/api"

        fun newInstance(apiKey: String): CloudMqtt {
            return CloudMqtt(HttpClient(CIO) {
                expectSuccess = false

                install(Auth) {
                    basic {
                        credentials {
                            BasicAuthCredentials(username = "", password = apiKey)
                        }
                    }
                }
                install(JsonFeature) {
                    serializer = JacksonSerializer()
                }
            })
        }
    }


    suspend fun getUserInfo(username: String): CloudMqttUser =
        client.get("$BASE_URL/user/$username")

    suspend fun createUser(username: String, password: String) =
        client.post<Unit>("$BASE_URL/user") {
            contentType(ContentType.Application.Json)
            body = mapOf("username" to username, "password" to password)
        }

    suspend fun updateUserPassword(username: String, newPassword: String) =
        client.put<Unit>("$BASE_URL/user/$username") {
            contentType(ContentType.Application.Json)
            body = newPassword
        }

    suspend fun deleteUser(username: String) =
        client.delete<Unit>("$BASE_URL/user/$username")


}

data class CloudMqttUser(
    val username: String,
    val acls: List<CloudMqttACL>
)

data class CloudMqttACL(
    val topic: String,
    val read: Boolean,
    val write: Boolean
)

