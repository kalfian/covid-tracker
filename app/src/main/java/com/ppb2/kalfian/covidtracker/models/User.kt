package com.ppb2.kalfian.covidtracker.models

data class User (
    val nik: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val gender: Int = 0,
    val pin: String = ""
) {
    fun toMap() {
        nik to nik
        email to email
        name to name
        gender to gender
        pin to pin
    }
}