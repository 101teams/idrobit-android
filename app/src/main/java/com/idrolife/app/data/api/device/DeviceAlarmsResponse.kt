package com.idrolife.app.data.api.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeviceAlarmsResponse(

	@SerialName("data")
	val data: DeviceAlarmData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class DeviceAlarmData(

	@SerialName("resultAlarms")
	val resultAlarms: List<ResultAlarmsItem?>? = null
)

@Serializable
data class ResultAlarmsItem(

	@SerialName("station")
	val station: String? = null,

	@SerialName("program")
	val program: String? = null,

	@SerialName("code")
	val code: String? = null,

	@SerialName("description")
	val description: String? = null
)
