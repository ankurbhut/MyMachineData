package com.practical.mydatamachine.ui.post.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.network.ApiEndpoints
import com.practical.mydatamachine.shared.network.domain.UserRepository
import com.practical.mydatamachine.shared.paging.CommonPagingSource
import com.practical.mydatamachine.ui.post.event.PostEvent
import com.practical.mydatamachine.ui.post.model.Posts
import com.practical.mydatamachine.ui.post.state.PostState
import com.practical.mydatamachine.ui.post.state.PostState.Companion.defaultValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PostViewModel : AdvanceBaseViewModel<PostEvent, PostState>(defaultValue) {

    fun getPostState() = _state

    override fun reduceState(
        currentState: PostState,
        event: PostEvent,
    ): PostState = when (event) {
        is PostEvent.OnApiFailure -> {
            currentState.copy(
                loadingState = Pair(
                    LoadingType.GET_POST_LIST_STATE, LoadingState.ERROR
                ), errorMessage = event.message
            )
        }

        is PostEvent.GetPosts -> {
            getPostList()
            currentState.copy(
                loadingState = Pair(
                    LoadingType.GET_POST_LIST_STATE, LoadingState.PROCESSING
                ),
            )
        }

        is PostEvent.OnPostResponse -> {
            currentState.copy(
                loadingState = Pair(
                    LoadingType.GET_POST_LIST_STATE, LoadingState.COMPLETED
                ), postsList = event.postsList
            )
        }

        is PostEvent.GetPostsDetail -> {
            getPostDetail(postId = event.postId)
            currentState.copy(
                loadingState = Pair(
                    LoadingType.GET_POST_DETAIL_STATE, LoadingState.PROCESSING
                ),
            )
        }

        is PostEvent.OnPostDetailResponse -> {
            currentState.copy(
                loadingState = Pair(
                    LoadingType.GET_POST_DETAIL_STATE, LoadingState.COMPLETED
                ), post = event.post
            )
        }
    }

    private fun getPostList() {
        val param = HashMap<String, Any>()
        viewModelScope.launch {
            Pager(
                PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                    prefetchDistance = 5,
                    initialLoadSize = 10
                )
            ) {
                CommonPagingSource(
                    ApiEndpoints.GET_POST, param
                )
            }.flow.cachedIn(viewModelScope).catch { exception ->
                exception.message?.let { _state.handleEvent(PostEvent.OnApiFailure(it)) }
            }.collect { result ->
                _state.handleEvent(PostEvent.OnPostResponse(result as PagingData<Posts>))
            }
        }
    }

    /**
     * Get Post Detail...
     */
    private fun getPostDetail(postId: Int) {
        viewModelScope.launch {
            UserRepository.getPostDetail(postId, Dispatchers.IO).onStart {

            }.onCompletion {

            }.catch { exception ->
                exception.message?.let { _state.handleEvent(PostEvent.OnApiFailure(it)) }
            }.collect { result ->
                if (result != null && result.isSuccessful) {
                    _state.handleEvent(PostEvent.OnPostDetailResponse(result.body()))
                } else result?.message()?.let { _state.handleEvent(PostEvent.OnApiFailure(it)) }

            }
        }
    }
}
