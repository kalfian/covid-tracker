package com.ppb2.kalfian.covidtracker.models

import java.io.Serializable

data class CheckInHistory (
    var id: String = "",
    var place_id: String = "",
    var place_name: String = "",
    var user_id: String = "",
    var user_place: String = "",
    var checkin_timestamp: Double = 0.0,
    var checkout_timestamp: Double = 0.0
): Serializable