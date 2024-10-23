package com.idrolife.app.service

import com.idrolife.app.data.api.ErrorResponse
import com.idrolife.app.data.api.HttpRoutes
import com.idrolife.app.data.api.auth.AuthData
import com.idrolife.app.data.api.auth.AuthRequest
import com.idrolife.app.data.api.auth.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthServiceImpl(
    private val client: HttpClient
): AuthService {
    override fun resetToken() {
        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .first().clearToken()
    }

    override suspend fun login(request: AuthRequest): Pair<AuthData?, String> {
        return try {
            val response = client.post {
                url(HttpRoutes.AUTH)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<AuthResponse>()
            Pair(response.data, "")
        } catch (e: RedirectResponseException) {
            Pair(null, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            val response = e.response.body<ErrorResponse>()
            Pair(null, response.error)
        } catch (e: ServerResponseException) {
            Pair(null, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(null, "Error: ${e.message}")
        }
    }

}