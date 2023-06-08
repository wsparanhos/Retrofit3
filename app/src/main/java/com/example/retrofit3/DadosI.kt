package com.example.retrofit3

import com.google.gson.annotations.SerializedName

data class DadosI(
    @SerializedName("ra") val ra: String? = null,
    @SerializedName("lat") val lat: String? = null,
    @SerializedName("lon") val lon: String? = null,
    @SerializedName("img") val img: String? = null
)
