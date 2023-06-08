package com.example.retrofit3

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServicePost {
    @Headers("Content-Type: application/json")
    @POST("/DS/dsApiIns.php")
    fun sendDados(@Body req: String ): Call<ResponseBody>
}