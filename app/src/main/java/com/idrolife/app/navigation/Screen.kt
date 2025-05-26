package com.idrolife.app.navigation

sealed class Screen(val route: String) {
    object Splash: Screen("splash")
    object Main: Screen("main")
    object Login: Screen("login")
    object Register: Screen("register")
    object ForgotPassword: Screen("forgot-password")
    object DetailDevice: Screen("detail-device")
    object SensorDevice: Screen("sensor-device")
    object SensorSoilMoisture: Screen("senor-soil-moisture")
    object IrrigationDevice: Screen("irrigation-device")
    object IrrigationConfig: Screen("irrigation-config")
    object IrrigationConfigNominalFlow: Screen("irrigation-config-nominal-flow")
    object IrrigationConfigGeneralSetting: Screen("irrigation-config-general-setting")
    object IrrigationConfigAdvanceConfig: Screen("irrigation-config-advance-config")
    object IrrigationConfigEVRadioStatus: Screen("irrigation-config-ev-radio-status")
    object IrrigationConfigEVConfig: Screen("irrigation-config-ev-config")
    object IrrigationSetting: Screen("irrigation-setting")
    object IrrigationSettingGeneralParameter: Screen("irrigation-setting-general-parameter")
    object CreatePlant: Screen("create-plant")
    object IrrigationSettingSensorManagement: Screen("irrigation-setting-sensor-management")
    object IrrigationStatus: Screen("irrigation-status")
    object IrrigationStatusProgramStatus: Screen("irrigation-status-program-status")
    object IrrigationStatusStationStatus: Screen("irrigation-status-station-status")
    object IrrigationStatusIdrosatStatus: Screen("irrigation-status-idrosat-status")
    object ManualEVStart: Screen("manual-ev-start")
    object ManualProgramStart: Screen("manual-program-start")
    object FertigationDevice: Screen("fertigation-device")
    object FertigationProgrammation: Screen("fertigation-programmation")
    object FertigationProgrammationECSetting: Screen("fertigation-programmation-ec-setting")
    object FertigationStatus: Screen("fertigation-status")
    object Map: Screen("map")
    object ChooseDevice : Screen("choose_device")
    object ChooseNetwork : Screen("choose_network")
    object NetworkSetup : Screen("network_setup/{ssid}")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}
