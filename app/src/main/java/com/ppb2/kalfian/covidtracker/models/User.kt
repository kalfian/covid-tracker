package com.ppb2.kalfian.covidtracker.models

import java.io.Serializable

data class User (
    var uid: String = "",
    var nik: String = "",
    var email: String = "",
    var name: String = "",
    var phoneNumber: String = "",
    var gender: Int = 0,
    var pin: String = "",
    var password: String = ""
): Serializable {
    fun toMap() {
        nik to nik
        email to email
        name to name
        gender to gender
        pin to pin
    }
}