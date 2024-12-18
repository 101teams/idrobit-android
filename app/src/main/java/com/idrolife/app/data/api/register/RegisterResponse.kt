package com.idrolife.app.data.api.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(

	@SerialName("data")
	val data: RegisterData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class User(

	@SerialName("updated_at")
	val updatedAt: String? = null,

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
data class RegisterData(

	@SerialName("user")
	val user: User? = null
)
