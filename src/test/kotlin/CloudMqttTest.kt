import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BasicAuthCredentials
import io.ktor.client.features.auth.providers.basic
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.http.hostWithPort
import io.ktor.http.toURI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@ExperimentalCoroutinesApi
class CloudMqttTest {

    private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
    private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

    private val testApiKey = "asfdkasfnmlkasmfef351561";

    private val client = HttpClient(MockEngine) {
        engine {
            addHandler { request ->

                when (request.url.fullUrl) {
                    "https://api.cloudmqtt.com/api/user/test" -> {
                        val responseHeaders =
                            headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))

                        val user = jacksonObjectMapper().writeValueAsString(CloudMqttUser(
                            "test",
                            acls = listOf()
                        ))
                        respond(user, headers = responseHeaders)
                    }
                    else -> error("Unhandled ${request.url.fullUrl}")
                }
            }
        }

        expectSuccess = false

        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = "", password = testApiKey)
                }
            }
        }
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    private val cloudMqtt = CloudMqtt(client)



    @Test
    fun testGetUserInfo() = runBlocking {
        val userInfo = cloudMqtt.getUserInfo("test")

        assert(userInfo.username == "test")
    }
}