package com.practical.mydatamachine.ui.post

import androidx.annotation.MainThread
import androidx.lifecycle.lifecycleScope
import com.practical.mydatamachine.R
import com.practical.mydatamachine.databinding.ActivityPostDetailBinding
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.base.activitybase.NavigationBaseActivity
import com.practical.mydatamachine.shared.extension.hide
import com.practical.mydatamachine.shared.extension.show
import com.practical.mydatamachine.shared.extension.showAlertDialog
import com.practical.mydatamachine.ui.post.event.PostEvent
import com.practical.mydatamachine.ui.post.model.Posts
import com.practical.mydatamachine.ui.post.state.PostState
import com.practical.mydatamachine.ui.post.viewmodel.PostViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class PostDetailActivity :
    NavigationBaseActivity<ActivityPostDetailBinding, PostEvent, PostState, PostViewModel>(
        ActivityPostDetailBinding::inflate, PostViewModel::class.java
    ) {

    override fun initUI() {
        setLoadingState(true)
    }

    override fun render(state: PostState) {
        lifecycleScope.launch {
            when (state.loadingState.first) {
                AdvanceBaseViewModel.LoadingType.GET_POST_DETAIL_STATE -> {
                    when (state.loadingState.second) {
                        AdvanceBaseViewModel.LoadingState.COMPLETED -> {
                            setLoadingState(false)
                            state.post?.let { onSetData(it) }
                        }

                        AdvanceBaseViewModel.LoadingState.ERROR -> {
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

    private fun onSetData(data: Posts) {
        binding?.txtTitle?.text = buildString {
            append("Title : ")
            append(data.title.toString())
        }
        binding?.txtDescription?.text = buildString {
            append("Body : ")
            append(data.body.toString())
        }
        binding?.txtPostId?.text = buildString {
            append("PostId : ")
            append(data.id.toString())
        }
        binding?.txtUserId?.text = buildString {
            append("UserId : ")
            append(data.userId.toString())
        }
    }

    override fun initDATA() {
        val postId = intent.getIntExtra("post_id", 0)
        lifecycleScope.launch {
            mViewModel?.getPostState()?.collectLatest {
                render(it)
            }
        }

        mViewModel?.getPostState()?.handleEvent(PostEvent.GetPostsDetail(postId))

    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding?.mainView?.hide()
            binding?.loader?.show()
        } else {
            binding?.mainView?.show()
            binding?.loader?.hide()
        }
    }

    @MainThread
    override fun onSuperBackPressed() {
        finish()
    }

    companion object {
        val TAG: String = PostDetailActivity::class.java.name
    }
}