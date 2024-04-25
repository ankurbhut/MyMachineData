package com.practical.mydatamachine.ui.post

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.practical.mydatamachine.R
import com.practical.mydatamachine.ui.post.event.PostEvent
import com.practical.mydatamachine.ui.post.state.PostState
import com.practical.mydatamachine.ui.post.viewmodel.PostViewModel
import com.practical.mydatamachine.databinding.ActivityPostBinding
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.base.activitybase.NavigationBaseActivity
import com.practical.mydatamachine.shared.callback.IItemViewListener
import com.practical.mydatamachine.shared.extension.hide
import com.practical.mydatamachine.shared.extension.show
import com.practical.mydatamachine.shared.extension.showAlertDialog
import com.practical.mydatamachine.ui.post.adapter.PostAdapter
import com.practical.mydatamachine.ui.post.adapter.stateadapter.PostLoadStateAdapter
import com.practical.mydatamachine.ui.post.model.Posts
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostActivity :
    NavigationBaseActivity<ActivityPostBinding, PostEvent, PostState, PostViewModel>(
        ActivityPostBinding::inflate, PostViewModel::class.java
    ), IItemViewListener {

    private var adapterNotification: PostAdapter? = null

    override fun initUI() {
        initView()
        setNotificationAdapter()
    }
    override fun render(state: PostState) {
        lifecycleScope.launch {
            when (state.loadingState.first) {
                AdvanceBaseViewModel.LoadingType.GET_POST_LIST_STATE -> {
                    when (state.loadingState.second) {
                        AdvanceBaseViewModel.LoadingState.COMPLETED -> {
                            binding?.loader?.hide()
                            state.postsList?.let { onNotificationData(it) }
                        }

                        AdvanceBaseViewModel.LoadingState.ERROR -> {
                            binding?.loader?.hide()
                            showAlertDialog(
                                title = getString(R.string.error),
                                message = state.errorMessage,
                                imageRes = R.drawable.app_logo,
                                positiveRes = R.string.ok,
                                onCancel = {},
                                onDismiss = {},
                            )
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    override fun initDATA() {
        mViewModel?.getPostState()?.handleEvent(PostEvent.GetPosts)
        lifecycleScope.launch {
            mViewModel?.getPostState()?.collectLatest {
                render(it)
            }
        }
    }

    private fun initView() {
        binding?.layoutErrorView?.hide()
    }

    private fun setNotificationAdapter() {
        binding?.layoutErrorView?.hide()
        binding?.rvItems?.show()
        binding?.rvItems?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            adapterNotification = PostAdapter(this@PostActivity)
            adapter =
                adapterNotification?.withLoadStateHeaderAndFooter(footer = PostLoadStateAdapter {/*make retry request*/ },
                    header = PostLoadStateAdapter {/*make retry request*/ })
            setLoadingState()
        }
    }

    private fun setLoadingState() {
        adapterNotification?.addLoadStateListener {
            if (it.source.append !is LoadState.Loading && it.source.prepend !is LoadState.Loading) {
                when (it.source.refresh) {
                    is LoadState.Loading -> {
                        if (adapterNotification?.itemCount == 0) {
                            binding?.rvItems?.hide()
                            binding?.layoutErrorView?.hide()
                            binding?.loader?.show()
                        } else {
                            binding?.rvItems?.show()
                            binding?.layoutErrorView?.hide()
                            binding?.loader?.hide()
                        }
                    }

                    is LoadState.Error -> {
                        binding?.loader?.hide()
                        binding?.rvItems?.hide()
                        binding?.layoutErrorView?.show()
                    }

                    else -> {
                        binding?.rvItems?.show()
                        binding?.layoutErrorView?.hide()
                        binding?.loader?.hide()
                    }
                }
            }
        }
    }

    private fun onNotificationData(result: PagingData<Posts>) {
        lifecycleScope.launch {
            adapterNotification?.submitData(result)
        }
    }


    override fun onItemClick(view: View?, position: Int?, actionType: Int?, vararg objects: Any?) {
        if (objects[0] is Posts) {
            val post = objects[0] as Posts
            post.id?.let { openPostDetailActivity(it) }
        }
    }

    companion object {
        val TAG: String = PostActivity::class.java.name
    }
}