package me.adamstroud.capsuledemo.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Headline(val main: String,
                    @Json(name = "print_headline")val printHeadline: String?) : Parcelable