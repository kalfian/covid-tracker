package com.ppb2.kalfian.covidtracker.utils

import com.ppb2.kalfian.covidtracker.models.PostLocationResponse
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface Api {
    @POST("user-journey/post-user-journey")
    fun postUserJourney(
        @QueryMap parameters: HashMap<String, Any>
    ): Call<PostLocationResponse>
}