package com.practical.mydatamachine.ui.post.state

import androidx.paging.PagingData
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.base.ViewState
import com.practical.mydatamachine.ui.post.model.Posts

data class PostState(
    val loadingState: Pair<AdvanceBaseViewModel.LoadingType, AdvanceBaseViewModel.LoadingState> = Pair(
        AdvanceBaseViewModel.LoadingType.EMPTY_SATE, AdvanceBaseViewModel.LoadingState.PROCESSING
    ), val errorMessage: String?,
    val postsList: PagingData<Posts>?,
    val post: Posts?
) : ViewState {

    companion object {
        val defaultValue = PostState(
            loadingState = Pair(
                AdvanceBaseViewModel.LoadingType.EMPTY_SATE,
                AdvanceBaseViewModel.LoadingState.PROCESSING
            ), errorMessage = null,
            postsList = null,
            post = null
        )
    }
}
