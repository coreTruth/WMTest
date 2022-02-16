package com.example.myapplication.data

import com.squareup.moshi.Json

data class FlickerSearchPhotoResponse(
    @Json(name = "photos")
    val photos: Photos,

    @Json(name = "stat")
    val stat: String
)