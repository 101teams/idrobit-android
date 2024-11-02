package com.idrolife.app.data.api.irrigation

import kotlinx.serialization.Serializable


@Serializable
data class IrrigationConfigNominalFlowRequest(
	val command: String? = null,
	val payload: Map<String, String>
)
