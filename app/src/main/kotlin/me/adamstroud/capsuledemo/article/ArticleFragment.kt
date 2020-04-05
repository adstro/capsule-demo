package me.adamstroud.capsuledemo.article

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import me.adamstroud.capsuledemo.R
import me.adamstroud.capsuledemo.databinding.FragmentArticleBinding
import me.adamstroud.capsuledemo.glide.GlideApp
import me.adamstroud.capsuledemo.model.Doc
import me.adamstroud.capsuledemo.model.Multimedia

class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    private val viewModel: ViewModel by viewModels {
        val args: ArticleFragmentArgs by navArgs()
        ViewModel.Factory(requireActivity().application, args)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setupWithNavController(findNavController())

        GlideApp.with(this)
            .load(viewModel.doc.multimedia.firstOrNull { it.cropName == Multimedia.CropName.MOBILE_MASTER_AT_3X }?.url)
            .centerCrop()
            .into(binding.image)

        binding.readArticle.setOnClickListener {
            CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                .build()
                .launchUrl(requireContext(), viewModel.doc.webUrl!!)
        }
    }

    class ViewModel(application: Application,
                    val doc: Doc) : AndroidViewModel(application) {
        val headline: String = doc.headline.main
        val leadParagraph: String = doc.leadParagraph
        val readArticleEnabled: Boolean = doc.webUrl != null

        class Factory(private val application: Application,
                      private val args: ArticleFragmentArgs) : ViewModelProvider.AndroidViewModelFactory(application) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
                return ViewModel(application, args.doc) as T
            }
        }
    }
}
