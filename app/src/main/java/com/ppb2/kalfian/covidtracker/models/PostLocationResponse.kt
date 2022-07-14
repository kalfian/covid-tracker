package com.ppb2.kalfian.covidtracker.models

import com.google.gson.annotations.SerializedName

data class PostLocationResponse (
    @SerializedName("data") var message : String,
    @SerializedName("success") var success : Boolean
)