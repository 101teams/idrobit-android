package com.idrolife.app.data.api.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SensorSatstatResponse(

	@SerialName("data")
	val data: SensorSatstatData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class SensorSatstatData(

	@SerialName("result")
	val result: String? = null,

	@SerialName("_stop")
	val stop: String? = null,

	@SerialName("S4")
	val s4: String? = null,

	@SerialName("_measurement")
	val measurement: String? = null,

	@SerialName("_start")
	val start: String? = null,

	@SerialName("S131")
	val s131: String? = null,

	@SerialName("device")
	val device: String? = null,

	@SerialName("S130")
	val s130: String? = null,

	@SerialName("table")
	val table: Int? = null
)
