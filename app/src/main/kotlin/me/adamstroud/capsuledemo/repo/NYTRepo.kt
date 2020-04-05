package me.adamstroud.capsuledemo.repo

import me.adamstroud.capsuledemo.api.WebService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NYTRepo @Inject constructor(private val webService: WebService) {
    suspend fun getArticles(query: String? = null,
                            page: Int? = null) = webService.getArticles(query, page)
}