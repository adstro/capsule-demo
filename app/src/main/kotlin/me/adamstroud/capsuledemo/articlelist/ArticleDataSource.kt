package me.adamstroud.capsuledemo.articlelist

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.*
import me.adamstroud.capsuledemo.model.ArticleResponse
import me.adamstroud.capsuledemo.model.Doc
import me.adamstroud.capsuledemo.repo.NYTRepo
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class ArticleDataSource(private val repo: NYTRepo,
                        private val query: String?) : PageKeyedDataSource<Int, Doc>(), CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Doc>
    ) {
        launch {
            val response = repo.getArticles(query = query, page = 0)

            if (response.isSuccessful) {
                callback.onResult(
                    response.body()?.response?.docs ?: emptyList(),
                    calculatePrevious(response),
                    calculateNext(response)
                )
            } else {
                Timber.w("Could not load articles -> $response")
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Doc>) {
        launch {
            val response = repo.getArticles(page = params.key, query = query)

            if (response.isSuccessful) {
                callback.onResult(response.body()?.response?.docs ?: emptyList(), calculateNext(response))
            } else {
                Timber.w("Could not load articles -> $response")
                callback.onResult(response.body()?.response?.docs ?: emptyList(), null)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Doc>) {
        launch {
            val response = repo.getArticles(page = params.key, query = query)

            if (response.isSuccessful) {
                callback.onResult(response.body()?.response?.docs ?: emptyList(), calculatePrevious(response))
            } else {
                Timber.w("Could not load articles -> $response")
                callback.onResult(response.body()?.response?.docs ?: emptyList(), null)
            }
        }
    }

    override fun invalidate() {
        job.cancelChildren()
        super.invalidate()
    }

    private fun calculatePrevious(response: Response<ArticleResponse>): Int? {
        val page = response.body()?.response?.meta?.offset?.div(PAGE_SIZE)

        return if (page == null || page < 1) {
            null
        } else {
            (page - 1).toInt()
        }

    }

    private fun calculateNext(response: Response<ArticleResponse>): Int? {
        return response.body()?.response?.meta?.offset?.div(PAGE_SIZE)?.plus(1)?.toInt()
    }

    class Factory(private val repo: NYTRepo, private val query: String? = null) : DataSource.Factory<Int, Doc>() {
        override fun create(): DataSource<Int, Doc> = ArticleDataSource(repo, query)
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}