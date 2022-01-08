package com.ppb2.kalfian.covidtracker.models

import java.io.Serializable

data class EmergencyModel (
    var emergencyId: String = "",
    var user_id: String = "",
    var user_name: String = "",
    var lat: Double = 0.0,
    var long: Double = 0.0,
    var status: Int = 0
): Serializable