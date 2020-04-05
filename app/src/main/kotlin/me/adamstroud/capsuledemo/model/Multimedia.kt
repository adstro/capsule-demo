package me.adamstroud.capsuledemo.model

import android.net.Uri
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Multimedia(val type: Type,
                      val width: Int,
                      val height: Int,
                      @Json(name = "url") val urlPath: String,
                      @Json(name = "crop_name") val cropName: CropName) : Parcelable {
    val url: Uri
        get() = Uri.parse("https://www.nytimes.com/${urlPath}")

    enum class Type {
        @Json(name = "image")
        IMAGE,

        UNSUPPORTED
    }

    enum class CropName {
        @Json(name = "superJumbo")
        SUPER_JUMBO,

        @Json(name = "thumbStandard")
        THUMB_STANDARD,

        @Json(name = "largeWidescreen1050")
        LARGE_WIDESCREEN_1050,

        @Json(name = "mobileMasterAt3x")
        MOBILE_MASTER_AT_3X,

        UNSUPPORTED
    }
}