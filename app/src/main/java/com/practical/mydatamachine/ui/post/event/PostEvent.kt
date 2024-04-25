package com.practical.mydatamachine.ui.post.event

import androidx.paging.PagingData
import com.practical.mydatamachine.shared.base.ViewIntent
import com.practical.mydatamachine.ui.post.model.Posts

sealed class PostEvent : ViewIntent {
    data class OnApiFailure(val message: String) : PostEvent()
    data object GetPosts : PostEvent()
    data class OnPostResponse(val postsList: PagingData<Posts>?) : PostEvent()

    data class GetPostsDetail(val postId: Int) : PostEvent()
    data class OnPostDetailResponse(val post: Posts?) : PostEvent()

}