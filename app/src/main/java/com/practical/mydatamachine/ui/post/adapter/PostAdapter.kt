package com.practical.mydatamachine.ui.post.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practical.mydatamachine.ui.post.adapter.diff.PostDiffUtilCallback
import com.practical.mydatamachine.databinding.RowPostBinding
import com.practical.mydatamachine.shared.callback.IItemViewListener
import com.practical.mydatamachine.ui.post.model.Posts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(
    private val mListener: IItemViewListener?,
) : PagingDataAdapter<Posts, PostAdapter.ViewHolder>(PostDiffUtilCallback()) {

    private val detailsCache = mutableMapOf<Int?, String>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int,
    ): ViewHolder {
        return ViewHolder(
           RowPostBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ViewHolder(val binding: RowPostBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(view: View) {
            mListener?.onItemClick(
                view, bindingAdapterPosition, IItemViewListener.CLICK, getItem(bindingAdapterPosition)
            )
        }

        fun bind(data: Posts?) {
            // Check if details are already cached
            if (detailsCache.containsKey(data?.id)) {
                // If cached, use the cached value
                binding.txtTitle.text = data?.title
                binding.txtDetail.text = detailsCache[data?.id]
            } else {
                // If not cached, compute details asynchronously
                data?.let { computeDetails(it) }
            }
//            binding.executePendingBindings()
        }

        private fun computeDetails(post: Posts) {
            // Perform heavy computation asynchronously
            CoroutineScope(Dispatchers.Default).launch {
                val startTime = System.currentTimeMillis()

                val details = performHeavyComputation(post)

                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime
                Log.d("HeavyComputation", "Time taken for item ${post.id}: $duration ms")

                // Cache the computed details
                detailsCache[post.id] = "$details & Time taken is:  $duration ms"

                // Update UI in the main thread
                withContext(Dispatchers.Main) {
                    binding.txtTitle.text = post.title
                    binding.txtDetail.text = "$details & Time taken is: $duration ms"
                    Log.d("HeavyComputation_Detail", details)
                }
            }
        }

        private suspend fun performHeavyComputation(post: Posts): String {
            // Simulate heavy computation by delaying for a few seconds
            delay(2000) // Adjust the delay as per your computation needs
            return "Additional details for post ${post.id}"
        }

        init {
            binding.root.setOnClickListener(this)
        }
    }
}