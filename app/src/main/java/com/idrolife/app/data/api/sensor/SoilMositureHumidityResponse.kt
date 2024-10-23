package com.idrolife.app.data.api.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoilMositureHumidityResponse(

	@SerialName("data")
	val data: SoilMositureHumidityData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class RhsItem(

	@SerialName("level")
	val level: String? = null,

	@SerialName("latitude")
	val latitude: String? = null,

	@SerialName("name")
	val name: String? = null,

	@SerialName("longitude")
	val longitude: String? = null
)

@Serializable
data class SoilMositureHumidityData(

	@SerialName("rhs")
	val rhs: List<RhsItem?>? = null
)
