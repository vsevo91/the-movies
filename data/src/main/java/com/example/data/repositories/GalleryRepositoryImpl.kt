package com.example.data.repositories

import android.util.Log
import androidx.paging.*
import com.example.data.pagingsources.GalleryPagingSource
import com.example.data.storages.ApiGalleryStorage
import com.example.data.storages.apistorage.entities.gallery.GalleryImageListResponse
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.entities.gallery.GalleryImageListPaged
import com.example.domain.repositories.GalleryRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.NETWORK_PAGE_SIZE
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    private val apiGalleryStorage: ApiGalleryStorage
) : GalleryRepository {

    private var attemptCounter = 1

    override suspend fun getGalleryByIdAndByPage(
        movieId: Int,
        page: Int,
        type: String
    ): GalleryImageListPaged {
        val response = apiGalleryStorage.getGalleryByIdPaged(movieId, page, type)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToGalleryImageListPaged()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 400)
                return GalleryImageListPaged(items = emptyList(), total = -1, totalPages = page)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getGalleryByIdAndByPage(movieId, page, type)
                } else {
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
                    throw Throwable(message = response.message())
                }
            } else {
                Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
                throw Throwable(message = response.message())
            }
        }
    }

    private fun calculateDelay(counter: Int): Long {
        val delay = counter / 10 * 100
        Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, delay.toString())
        return delay.toLong()
    }

    private fun makeErrorMessage(response: Response<*>): String {
        return buildString {
            appendLine("Unsuccessful request form \"${this@GalleryRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    override fun getGalleryByIdStream(movieId: Int, type: String): Flow<PagingData<GalleryImage>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = {
                GalleryPagingSource { page ->
                    getGalleryByIdAndByPage(movieId, page, type)
                }
            }
        ).flow
    }

    private fun GalleryImageListResponse.mapToGalleryImageListPaged(): GalleryImageListPaged {
        val itemsFromResponse = this.items
        val items = mutableListOf<GalleryImage>()
        itemsFromResponse.forEach {
            val item = GalleryImage(
                imageUrl = it.imageUrl,
                previewUrl = it.previewUrl
            )
            items.add(item)
        }
        return GalleryImageListPaged(
            total = this.total,
            totalPages = this.totalPages,
            items = items
        )
    }
}