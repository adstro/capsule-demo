package me.adamstroud.capsuledemo.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(val hits: Long,
                val offset: Long,
                val time: Long)