package me.adamstroud.capsuledemo.articlelist

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.adamstroud.capsuledemo.R
import me.adamstroud.capsuledemo.databinding.ListItemArticleBinding
import me.adamstroud.capsuledemo.glide.GlideApp
import me.adamstroud.capsuledemo.model.Doc
import me.adamstroud.capsuledemo.model.Multimedia

class ArticleAdapter : PagedListAdapter<Doc, ArticleAdapter.ViewHolder>(DiffCallback) {
    private val _clicks = MutableLiveData<Doc>()
    val clicks: LiveData<Doc>
        get() = _clicks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        private var doc: Doc? = null

        init {
            binding.root.setOnClickListener { _clicks.postValue(doc) }
        }

        fun bind(doc: Doc?) {
            this.doc = doc

            binding.headline.text = doc?.headline?.main.orEmpty()

            GlideApp.with(binding.root)
                .load(doc?.multimedia?.firstOrNull { it.cropName == Multimedia.CropName.THUMB_STANDARD }?.url)
                .circleCrop()
                .placeholder(R.drawable.ic_placeholder_48dp)
                .into(binding.icon)

            binding.publishDate.text = doc?.publishDate?.toInstant()?.toEpochMilli()?.let {
                DateUtils.formatDateTime(binding.root.context, it, DateUtils.FORMAT_ABBREV_ALL)
            }
        }
    }

    object DiffCallback: DiffUtil.ItemCallback<Doc>() {
        override fun areItemsTheSame(oldItem: Doc, newItem: Doc): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Doc, newItem: Doc): Boolean {
            return oldItem == newItem
        }
    }
}