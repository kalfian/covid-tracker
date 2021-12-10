package com.ppb2.kalfian.covidtracker.models

import java.io.Serializable

data class VaccineCert (
    var uid: String = "",
    var title: String = "",
    var image: String = "",
    var user_uid: String = "",
    var seq: Int = 1
): Serializable