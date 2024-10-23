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

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}
