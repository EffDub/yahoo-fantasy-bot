@file:JvmName("Server")

package webserver

import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.request.host
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shared.EnvVariables
import shared.Postgres

private const val DEFAULT_PORT = 4567

private lateinit var service: OAuth20Service

fun main() {
    embeddedServer(Netty, EnvVariables.Port.variable?.toInt() ?: DEFAULT_PORT) {
        routing {
            get("/") {
                if (Postgres.latestTokenData == null) {
                    println("User is not authenticated.  Sending to Yahoo.")
                    call.respondRedirect(authenticationUrl(call.request.origin.scheme + "://" + call.request.host()))
                } else {
                    println("User is already authenticated.  Not sending to Yahoo.")
                    call.respondText("You are already authenticated with Yahoo's servers.")
                }
            }

            get("/auth") {
                val token = withContext(Dispatchers.IO) {
                    service.getAccessToken(call.request.queryParameters["code"])
                }

                Postgres.saveTokenData(token)
                println("Access token received.  Authorized successfully.")
                call.respondText("You are authorized")
            }
        }
    }.start(true)
}

private fun authenticationUrl(url: String): String {
    println("Initial authorization...")

    service = ServiceBuilder(EnvVariables.YahooClientId.variable)
        .apiSecret(EnvVariables.YahooClientSecret.variable)
        .callback("$url/auth")
        .build(YahooApi20.instance())

    return service.authorizationUrl
}
