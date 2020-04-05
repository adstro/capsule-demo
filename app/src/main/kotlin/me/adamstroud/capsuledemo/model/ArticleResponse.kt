package me.adamstroud.capsuledemo.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArticleResponse(val status: Status,
                           val copyright: String,
                           val response: Response) {
    @JsonClass(generateAdapter = true)
    data class Response(val docs: List<Doc>, val meta: Meta)
}