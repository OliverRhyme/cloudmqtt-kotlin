package com.coldfire.cloudmqtt

import com.coldfire.cloudmqtt.model.CloudMqttACL
import com.coldfire.cloudmqtt.model.CloudMqttUser
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
                expectSuccess = true

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

        private fun getAPIEndpoint(path: String) = "$BASE_URL/$path"
    }


    suspend fun getUserInfo(username: String): CloudMqttUser =
        client.get(getAPIEndpoint("user/$username"))

    suspend fun createUser(username: String, password: String) =
        client.post<Unit>(getAPIEndpoint("user")) {
            contentType(ContentType.Application.Json)
            body = mapOf("username" to username, "password" to password)
        }

    suspend fun updateUserPassword(username: String, newPassword: String) =
        client.put<Unit>(getAPIEndpoint("user/$username")) {
            contentType(ContentType.Application.Json)
            body = newPassword
        }

    suspend fun deleteUser(username: String) =
        client.delete<Unit>(getAPIEndpoint("user/$username"))


    suspend fun createAclRule(
        type: ACLType,
        username: String,
        acl: CloudMqttACL
    ) {
        client.post<Unit>(getAPIEndpoint("acl")) {
            contentType(ContentType.Application.Json)

            val mapper = jacksonObjectMapper()

            val metadata = mapOf<String, Any>(
                "type" to type,
                "username" to username,
            )

            val aclMap = mapper.convertValue<Map<String, Any>>(acl)

            val values =  metadata + aclMap

            body = values
        }
    }

    suspend fun deleteAclRule(username: String, topic: String) {
        client.delete<Unit>(getAPIEndpoint("acl")) {
            contentType(ContentType.Application.Json)
            body = mapOf(
                "username" to username,
                "topic" to topic
            )
        }
    }

}

