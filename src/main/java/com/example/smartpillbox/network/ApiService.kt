package com.example.smartpillbox.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("setReminder")
    fun setReminder(
        @Query("slot") slot: Int,
        @Query("period") period: Int,
        @Query("hour") hour: Int,
        @Query("minute") minute: Int,
        @Query("dosage") dosage: Int
    ): Call<Void>

    @GET("finishMed")
    fun finishMed(): Call<Void>
}