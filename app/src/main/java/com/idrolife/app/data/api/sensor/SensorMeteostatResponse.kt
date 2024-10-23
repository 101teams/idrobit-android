package com.idrolife.app.data.api.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SensorMeteostatResponse(

	@SerialName("data")
	val data: SensorMeteostatData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class SensorMeteostatData(

	@SerialName("_measurement")
	val measurement: String? = null,

	@SerialName("_start")
	val start: String? = null,

	@SerialName("M4")
	val m4: String? = null,

	@SerialName("M5")
	val m5: String? = null,

	@SerialName("M6")
	val m6: String? = null,

	@SerialName("M31")
	val m31: String? = null,

	@SerialName("M7")
	val m7: String? = null,

	@SerialName("M8")
	val m8: String? = null,

	@SerialName("M33")
	val m33: String? = null,

	@SerialName("M12")
	val m12: String? = null,

	@SerialName("result")
	val result: String? = null,

	@SerialName("_stop")
	val stop: String? = null,

	@SerialName("device")
	val device: String? = null,

	@SerialName("table")
	val table: Int? = null
)
