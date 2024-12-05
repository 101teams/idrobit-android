package com.idrolife.app.data.api.device

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlantRequest(
    val code: String,
    val name: String,
    val password: String,
    val type: String,
    val coordinate: String,
    val company: String,
)