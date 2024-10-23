package com.idrolife.app.data.api.irrigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class IrrigationConfigNominalFlowResponse(

	@SerialName("data")
	val data: IrrigationConfigNominalFlowData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class IrrigationConfigNominalFlowData(

	@SerialName("result")
	val result: String? = null,

	@SerialName("_stop")
	val stop: String? = null,

	@SerialName("_measurement")
	val measurement: String? = null,

	@SerialName("_start")
	val start: String? = null,

	@SerialName("device")
	val device: String? = null,

	@SerialName("table")
	val table: Int? = null,

	@Transient val dynamicFields: Map<String, String> = emptyMap()
)

data class IrrigationConfigNominalFlowDataProduct (
	val evSerial: String? = null,
	val station: String? = null,
	val pump: String? = null,
	val master: String? = null,
	val nominalValue: String? = null,
	val auto: Boolean? = null
)