package com.practical.mydatamachine.ui.post.adapter.stateadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practical.mydatamachine.databinding.PagingStateAdapterNotificationBinding
import com.practical.mydatamachine.shared.extension.hide
import com.practical.mydatamachine.shared.extension.show
import com.practical.mydatamachine.utility.ViewUtil

class PostLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<PostLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Error) {
            holder.mBinding.loadStateRetry.hide()
            holder.mBinding.loadStateErrorMessage.hide()
            holder.mBinding.loadStateProgress.layoutLoadingDefault.hide()
            loadState.error.localizedMessage?.let { ViewUtil.printLog("error", it.toString()) }
            //Toast.makeText(holder.binding.root.context,loadState.error.localizedMessage,Toast.LENGTH_SHORT).show()
            //holder.binding.loadStateErrorMessage.text = loadState.error.localizedMessage
        } else if (loadState is LoadState.Loading) {
            holder.mBinding.loadStateProgress.layoutLoadingDefault.show()
            holder.mBinding.loadStateRetry.hide()
            holder.mBinding.loadStateErrorMessage.hide()
        }

        holder.mBinding.loadStateRetry.setOnClickListener {
            retry.invoke()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {

        return LoadStateViewHolder(
            PagingStateAdapterNotificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    inner class LoadStateViewHolder(val mBinding: PagingStateAdapterNotificationBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        override fun onClick(view: View) {

        }
    }
}