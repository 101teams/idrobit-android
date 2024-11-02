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
	var evSerial: String? = null,
	var station: String? = null,
	var pump: String? = null,
	var master: String? = null,
	var nominalValue: String = "0",
	var auto: Boolean = false
)

data class IrrigationConfigGeneralSatConfig (
	var plantOperationStatus: String? = "",
	var password: String? = "",
	var solarIntensity: String? = "",
	var windSpeed: String? = "",
	var evMaster: String? = "",
	var ecCommand: String? = "",
	var pulsesFlow: String? = "",
	var solarIrradiation: String? = "",
	var windSensor: String? = "",
	var temperature: String? = "",
	var humidity: String? = "",
	var maxActiveProgram: String? = "",
	var entry1: String? = "",
	var entry2: String? = "",
	var entry1forDelay: String? = "",
	var entry2forDelay: String? = "",
	var flowOffTolerance: String? = "",
	var flowOff: String? = "",
	var flowAlarmDelay: String? = "",
	var entry3: String? = "",
	var entry4: String? = "",
	var entry3forDelay: String? = "",
	var entry4forDelay: String? = "",
	var pressureMin: String? = "",
	var pressureMax: String? = "",
	var delayAlarmTimeLowPressure: String? = "",
	var delayAlarmTimeHighPressure: String? = "",
)

data class IrrigationConfigGeneralPumpConfig (
	var pulses: String? = "",
	var pumpDeactivationDelay: String? = "",
)

data class IrrigationConfigGeneralMVConfig (
	var delayBetweenMSandEV: String? = "",
)

data class IrrigationConfigAdvanceConfig (
	var openedCircuit: String? = "",
	var acknowledgePulseTime: String? = "",
	var minimumAmpere: String? = "",
	var activationDelayMaster: String? = "",
	var activationDelayEV: String? = "",
	var evHoldingVoltage: String? = "",
	var triggerPulseTime: String? = "",
)

data class IrrigationConfigEVRadioStatus (
	var serialID: String? = "",
	var group: String? = "",
	var batteryLevel: String? = "",
	var signal: String? = "",
	var goodData: String? = "",
	var errorData: String? = "",
	var errorPercentage: String? = "",
)

data class IrrigationConfigEVConfigList (
	var evSerial: String? = null,
	var station: String? = null,
	var pump: String? = null,
	var master: String? = null,
	var nominalValue: String? = null,
	var index: Int = 0,
)