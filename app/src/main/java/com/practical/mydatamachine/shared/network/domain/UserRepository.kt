package com.practical.mydatamachine.shared.network.domain

import com.practical.mydatamachine.BuildConfig
import com.practical.mydatamachine.shared.network.client.RetroClient.okHttpClient
import com.practical.mydatamachine.shared.network.client.createWebService
import com.practical.mydatamachine.shared.network.data.BaseApiResponse
import com.practical.mydatamachine.ui.post.model.Posts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


object UserRepository : BaseApiResponse(), CoroutineScope {
    val userApiClient = createWebService(
        BuildConfig.SERVER_URL,
        UserApiService::class.java,
        okHttpClient = okHttpClient
    )

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun getPostDetail(
        postId: Int,
        dispatcher: CoroutineContext,
    ): Flow<Response<Posts>?> {
        return flow<Response<Posts>?> {
            kotlin.runCatching {
                emit(userApiClient.getPostDetail(postId))
            }
        }.flowOn(dispatcher)
    }

}