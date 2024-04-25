package com.practical.mydatamachine.shared.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practical.mydatamachine.shared.network.ApiEndpoints
import com.practical.mydatamachine.shared.network.domain.UserRepository
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.math.max

class CommonPagingSource(
    private val type: String,
    private val requestParam: MutableMap<String, Any>,
    private val limit: Int = 10
) : PagingSource<Int, Any>() {
    private val startingKey: Int get() = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {

        val pageIndex = params.key ?: startingKey
//        val range = pageIndex.until(pageIndex + params.loadSize)

        return try {
            val response = getApiResponse<Any>(type, limit, pageIndex)

            if (response?.isSuccessful == true && response.body().isNullOrEmpty().not()) {

                val prevKey = if (ensureValidNextKey(pageIndex)) pageIndex - 1 else null
                val count = response.body()?.size ?: 0
                val nextKey = if (ensureValidNextKey(count)) pageIndex + 1 else null

                LoadResult.Page(
                    data = response.body() ?: mutableListOf(),
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(Exception(response?.message()))
            }
        } catch (e: IOException) {
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        }
    }

    //TODO Modify after multiple type response
    private suspend fun <T> getApiResponse(
        type: String, limit: Int, pageIndex: Int
    ): Response<out ArrayList<out Any>>? {
        val response = when (type) {
            ApiEndpoints.GET_POST -> {
                UserRepository.userApiClient.getPosts(pageIndex, limit)
            }

          /*  ApiEndpoints.GET_ACTIVE_MINING_USERS -> {
                RetroClient.HOME_API_SERVICE.getMiningUsers(requestParam)
            }*/

            else -> null
        }
        return response
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Any>): Int? {
        // In our case we grab the item closest to the anchor position
        // then return its id - (state.config.pageSize / 2) as a buffer

        val anchorPosition = state.anchorPosition //?: return null
        val article = state.closestItemToPosition(anchorPosition ?: 0) ?: return null
        return ensureValidKey(key = (anchorPosition)?.minus((state.config.pageSize / 2)) ?: 0)

        /*    val anchorPosition = state.anchorPosition ?: return null
            val data = state.closestPageToPosition(anchorPosition) ?: return null
            return data.prevKey?.plus(1) ?: data.nextKey?.minus(1)*/


        //This is use for the jumping to get specific Key for the network call.
        /*val anchorPosition = state.anchorPosition ?: return null
        val pageIndex = anchorPosition / PAGE_SIZE

        return pageIndex*/


    }

    /**
     * Makes sure the paging key is never less than [startingKey]
     */
    private fun ensureValidKey(key: Int) = max(startingKey, key)

    /**
     * Makes sure the next key is never more than [Data Count]
     */
    private fun ensureValidNextKey(count: Int) = count >= limit

}
