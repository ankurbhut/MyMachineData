package com.practical.mydatamachine.shared.network.domain


import com.practical.mydatamachine.ui.post.model.Posts
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    @GET("posts")
    suspend fun getPosts(@Query("_page") page: Int, @Query("_limit") limit: Int): Response<ArrayList<Posts>>

    @GET("/posts/{postId}")
    suspend fun getPostDetail(@Path("postId") postId: Int): Response<Posts>?
}