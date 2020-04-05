package me.adamstroud.capsuledemo.model

import android.net.Uri
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
@JsonClass(generateAdapter = true)
data class Doc(@Json(name = "_id") val id: String,
               val abstract: String,
               @Json(name = "web_url") val webUrl: Uri?,
               val headline: Headline,
               val multimedia: List<Multimedia>,
               @Json(name = "lead_paragraph") val leadParagraph: String,
               @Json(name = "document_type") val type: Type,
               @Json(name = "pub_date") val publishDate: OffsetDateTime) : Parcelable {
    enum class Type {
        @Json(name = "article")
        ARTICLE,

        @Json(name = "recipe")
        RECIPE,

        @Json(name = "multimedia")
        MULTIMEDIA
    }
}