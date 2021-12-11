package com.ppb2.kalfian.covidtracker.models

import java.io.Serializable

data class TestCovid (
    var id: String = "",
    var is_positive: Boolean = false,
    var link: String = "",
    var title: String = "",
    var uid: String = "",
    var valid_date: Double = 0.0
): Serializable