package com.ppb2.kalfian.covidtracker.models

import java.io.Serializable

data class Place (
    var id: String = "",
    var banner: String = "",
    var quota: Int = 0,
    var name: String = "",
    var need_vaccine: Int = 0,
    var need_test: String = "",
    var lat: Double = 0.0,
    var long: Double = 0.0
): Serializable