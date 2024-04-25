package com.practical.mydatamachine.ui.post.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.practical.mydatamachine.ui.post.model.Posts

class PostDiffUtilCallback : DiffUtil.ItemCallback<Posts>() {

    override fun areItemsTheSame(
        oldItem: Posts,
        newItem: Posts,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Posts,
        newItem: Posts,
    ): Boolean {
        return oldItem.id == newItem.id
    }
}