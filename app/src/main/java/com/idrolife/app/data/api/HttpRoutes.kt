package com.idrolife.app.data.api

import com.idrolife.app.utils.Constants

object HttpRoutes {
    const val v1 = "/api/v1"
    const val AUTH = "${Constants.API_URL}${v1}/login"
    const val DEVICE = "${Constants.API_URL}${v1}/device"
    const val STAT = "${Constants.API_URL}${v1}/data/last"
    const val RH = "${Constants.API_URL}${v1}/rh"
    const val DEVICE_CONTROL = "${Constants.API_URL}${v1}/device/control"
    const val DEVICE_RAW_CONTROL = "${Constants.API_URL}${v1}/device/raw-control"
    const val DEVICE_GEO = "${Constants.API_URL}${v1}/device-geo"
}