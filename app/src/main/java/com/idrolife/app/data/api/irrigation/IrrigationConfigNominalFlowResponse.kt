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
	var evSerialKey: String? = null,
)

data class IrrigationSettingGeneralParameter (
	var programName: String? = "",
	var programMode: String? = "",
	var minifertProgramRelated: String? = "",
	var choiceTimeMode: String? = "",
	var cycletime: String? = "",
	var delayBetweenStation: String? = "",
	var delayBetweenCycle: String? = "",
	var startMode: String? = "",
	var biweeklyCalendar: String? = "",
	var activeWeek: String? = "",
	var skippedDays: String? = "",
	var daysBeforeStart: String? = "",
	var flowMode: String? = "",
	var timeMode: String? = "",
)

data class IrrigationSettingScheduleStart (
	var activate: String? = "",
	var startHour: String? = "",
	var startMinute: String? = "",
	var endHour: String? = "",
	var endMinute: String? = "",
	var cycle: String? = "",
)

data class IrrigationSettingSensorManagement (
	var humiditySensorType: String? = "",
	var waterBudget: String? = "",
	var programStop: String? = "",
	var programStandBy: String? = "",
	var programStart: String? = "",
	var programSkip: String? = "",
	var lowTemp: String? = "",
	var highTemp: String? = "",
	var lowHumidity: String? = "",
	var highHumidity: String? = "",
	var humiditySensorLevel: String? = "",
	var waterBudgetAuto: Boolean = false,
)

data class IrrigationStatusProgramStatus (
	var index: Int = 0,
	var status: String? = "",
	var stationUsed: String? = "",
	var remainingTime: String? = "",
)

data class IrrigationStatusStationStatus (
	var index: Int = 0,
	var station: String? = "",
	var status: Boolean = false,
	var action: String? = "",
	var remainingTime: String? = "",
)

data class IrrigationStatusIdrosatStatus (
	var name: String? = "",
	var value: String? = "",
)

data class ManualStartProgram (
	var name: String? = "",
	var value: String? = "",
)

data class FertigationStatus (
	var counterPrincipal: String? = "",
	var counter1: String? = "",
	var counter2: String? = "",
	var counter3: String? = "",
	var counter4: String? = "",
	var ec: String? = "",
	var ph: String? = "",
	var numberOfAlarm: String? = "",
	var activeProgram: String? = "",
)

data class FertigationProgrammation(
	var hysteresis: String? = "",
	var checkEvery: String? = "",
	var checkEveryType: String? = "",
	var setpointEC: String? = "",
	var setpointPh: String? = "",
	var dosagePh: String? = "",
)

data class StationDuration (
	var station: String,
	var group: String,
	var ev: String,
	var status: String,
	var flowMode: String,
	var hour: String,
	var minute: String,
	var second: String,
	var volume: String,
)