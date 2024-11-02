package com.idrolife.app.di

import android.content.Context
import com.idrolife.app.data.api.ErrorResponse
import com.idrolife.app.data.api.UnauthorizedException
import com.idrolife.app.data.api.UnprocessableEntityException
import com.idrolife.app.service.AuthService
import com.idrolife.app.service.AuthServiceImpl
import com.idrolife.app.service.DeviceService
import com.idrolife.app.service.DeviceServiceImpl
import com.idrolife.app.utils.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): PrefManager {
        return PrefManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthService(@ApplicationContext context: Context): AuthService {
        val prefManager = PrefManager(context)
        val client = HttpClient(Android) {
            expectSuccess = true

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    val clientException = cause as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = clientException.response
                    if (exceptionResponse.status == HttpStatusCode.UnprocessableEntity) {
                        val firstError = (exceptionResponse.body() as? ErrorResponse)?.error
                        throw UnprocessableEntityException(
                            exceptionResponse,
                            firstError ?: "Something went wrong"
                        )
                    }
                }
            }

            install(Logging) {
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json( Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(prefManager.getToken(), "")
                    }
                }
            }
        }

        return AuthServiceImpl(client)
    }



    @Provides
    @Singleton
    fun provideDeviceService(@ApplicationContext context: Context): DeviceService {
        val prefManager = PrefManager(context)
        val client = HttpClient(Android) {
            install(Logging) {
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json( Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(prefManager.getToken(), "")
                    }
                }
            }

            expectSuccess = true

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    val clientException = cause as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = clientException.response
                    if (exceptionResponse.status == HttpStatusCode.Unauthorized) {
                        val firstError = (exceptionResponse.body() as? ErrorResponse)?.error
                        throw UnauthorizedException(
                            exceptionResponse,
                            firstError ?: "Please login first"
                        )
                    }
                }
            }
        }

        return DeviceServiceImpl(client)
    }
}
