package com.idrolife.app.data.api.device

import kotlinx.serialization.Serializable

@Serializable
data class EditPlantRequest(
    val name: String,
)