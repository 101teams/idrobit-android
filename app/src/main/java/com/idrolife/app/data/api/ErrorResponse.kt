package com.idrolife.app.data.api

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: String,
    val error: String
)

