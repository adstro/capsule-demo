package me.adamstroud.capsuledemo.articlelist

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import me.adamstroud.capsuledemo.CapsuleDemoApplication
import me.adamstroud.capsuledemo.databinding.FragmentArticleListBinding
import me.adamstroud.capsuledemo.model.Doc
import me.adamstroud.capsuledemo.repo.NYTRepo
import timber.log.Timber
import javax.inject.Inject

class ArticleListFragment : Fragment() {
    private val viewModel: ViewModel by viewModels { ViewModelProvider.AndroidViewModelFactory(requireActivity().application) }
    private var binding: FragmentArticleListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.appbar?.toolbar?.setupWithNavController(findNavController())

        viewModel.listLoaded.observe(viewLifecycleOwner) {
            binding?.articleList?.visibility = View.VISIBLE
            binding?.progress?.visibility = View.GONE
        }

        binding?.articleList?.let {
            it.layoutManager = LinearLayoutManager(context)
            it.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            it.adapter = ArticleAdapter()

            (it.adapter as ArticleAdapter).clicks.observe(viewLifecycleOwner) { doc ->
                findNavController().navigate(ArticleListFragmentDirections.select(doc))
            }
        }

        viewModel.articles.observe(viewLifecycleOwner) {
            (binding?.articleList?.adapter as ArticleAdapter?)?.submitList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    class ViewModel(application: Application) : AndroidViewModel(application) {
        @Inject
        lateinit var nytRepo: NYTRepo

        val articles: LiveData<PagedList<Doc>>
        private val _listLoaded = MutableLiveData<Any>()
        val listLoaded: LiveData<Any>
            get() = _listLoaded

        init {
            (application as CapsuleDemoApplication).applicationComponent.inject(this)

            articles = ArticleDataSource.Factory(nytRepo).toLiveData(pageSize = ArticleDataSource.PAGE_SIZE,
                boundaryCallback = object : PagedList.BoundaryCallback<Doc>() {
                    override fun onItemAtFrontLoaded(itemAtFront: Doc) {
                        Timber.d("onItemAtFrontLoaded")
                        _listLoaded.postValue(true)
                    }

                    override fun onZeroItemsLoaded() {
                        Timber.d("onZeroItemsLoaded")
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: Doc) {
                        Timber.d("onItemAtEndLoaded")
                    }
                })
        }
    }
}