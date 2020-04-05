package me.adamstroud.capsuledemo.api

import me.adamstroud.capsuledemo.BuildConfig
import me.adamstroud.capsuledemo.model.ArticleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {
    @GET("svc/search/v2/articlesearch.json?api-key=${BuildConfig.API_KEY}")
    suspend fun getArticles(@Query("q") query: String?,
                            @Query("page") page: Int?,
                            @Query("fq") fq: String = "document_type:\"article\"",
                            @Query("sort") sort: String = "newest"): Response<ArticleResponse>
}