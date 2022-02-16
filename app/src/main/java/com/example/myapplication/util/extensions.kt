package com.example.myapplication.util

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.load(serverId: String, id: String, secret: String) {
    Picasso.get()
        .load(generateImageUrl(serverId, id, secret))
        .into(this)
}

fun generateImageUrl(serverId: String, id: String, secret: String) =
    "https://live.staticflickr.com/" + serverId + "/" + id +"_" + secret + ".jpg"