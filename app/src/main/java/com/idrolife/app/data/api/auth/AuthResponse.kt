package com.idrolife.app.data.api.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
	@SerialName("data")
	val data: AuthData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class User(

	@SerialName("remember_me_token")
	val rememberMeToken: String? = null,

	@SerialName("is_email_confirmed")
	val isEmailConfirmed: Boolean? = null,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("is_evreport")
	val isEvreport: Boolean? = null,

	@SerialName("last_name")
	val lastName: String? = null,

	@SerialName("created_at")
	val createdAt: String? = null,

	@SerialName("id")
	val id: Int? = null,

	@SerialName("first_name")
	val firstName: String? = null,

	@SerialName("email")
	val email: String? = null
)

@Serializable
data class Token(

	@SerialName("type")
	val type: String? = null,

	@SerialName("token")
	val token: String? = null
)

@Serializable
data class AuthData(

	@SerialName("user")
	val user: User? = null,

	@SerialName("token")
	val token: Token? = null
)
