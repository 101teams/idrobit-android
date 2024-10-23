package com.idrolife.app.data.api.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceListResponse(

	@SerialName("data")
	val data: DeviceListData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class DevicesItem(

	@SerialName("hw_version")
	val hwVersion: String? = null,

	@SerialName("code")
	val code: String? = null,

	@SerialName("role")
	val role: String? = null,

	@SerialName("system_pressure")
	val systemPressure: String? = null,

	@SerialName("created_at")
	val createdAt: String? = null,

	@SerialName("firmware_version")
	val firmwareVersion: String? = null,

	@SerialName("type")
	val type: String? = null,

	@SerialName("mac_address_device")
	val macAddressDevice: String? = null,

	@SerialName("fw_idrosat")
	val fwIdrosat: String? = null,

	@SerialName("is_alarm")
	val isAlarm: Boolean? = null,

	@SerialName("password")
	val password: String? = null,

	@SerialName("max_devices")
	val maxDevices: String? = null,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("mac_address")
	val macAddress: String? = null,

	@SerialName("company")
	val company: String? = null,

	@SerialName("id")
	val id: Int? = null,

	@SerialName("client_name")
	val clientName: String? = null,

	@SerialName("flow")
	val flow: String? = null,

	@SerialName("coordinate")
	val coordinate: String? = null,

	@SerialName("is_fast_watering")
	val isFastWatering: Boolean? = null,

	@SerialName("consumption")
	val consumption: Int? = null,

	@SerialName("is_alarm_device")
	val isAlarmDevice: String? = null,

	@SerialName("fw_esp32")
	val fwEsp32: String? = null,

	@SerialName("active_station")
	val activeStation: String? = null,

	@SerialName("active_program")
	val activeProgram: String? = null,

	@SerialName("name")
	val name: String? = null,

	@SerialName("phone_number")
	val phoneNumber: String? = null,

	@SerialName("status")
	val status: String? = null,

	var responseDateTime: String? = null,
)

@Serializable
data class DeviceListData(

	@SerialName("devices")
	val devices: List<DevicesItem?>? = null
)
