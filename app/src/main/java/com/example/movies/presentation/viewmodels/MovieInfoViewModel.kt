package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.entities.gallery.GalleryImageListPaged
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieSimilar
import com.example.domain.entities.series.Series
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.domain.usecases.*
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_IMAGES_FOR_HORIZONTAL_PREVIEW
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MovieInfoViewModel @Inject constructor(
    private val getMovieFullInfoByIdUseCase: GetMovieFullInfoByIdUseCase,
    private val getStaffListByMovieIdUseCase: GetStaffListByMovieIdUseCase,
    private val getGalleryByMovieIdUseCase: GetGalleryByMovieIdUseCase,
    private val getSimilarMoviesByMovieIdUseCase: GetSimilarMoviesByMovieIdUseCase,
    private val getSeriesByIdUseCase: GetSeriesByIdUseCase,
    private val addMovieToUserCollectionUseCase: AddMovieToUserCollectionUseCase,
    private val deleteMovieFromUserCollectionUseCase: DeleteMovieFromUserCollectionUseCase,
    getUserCollectionsUseCase: GetUserCollectionsUseCase
) : ViewModel() {

    private val _connectionErrorState = MutableLiveData(false)
    val connectionErrorState: LiveData<Boolean> = _connectionErrorState

    private val _otherErrorState = MutableLiveData(false)
    val otherErrorState: LiveData<Boolean> = _otherErrorState

    private val _movieLiveData = MutableLiveData<MovieFullInfo?>()
    val movieLiveData: LiveData<MovieFullInfo?> get() = _movieLiveData

    private val _staffRelatedToMovieLiveData = MutableLiveData<List<StaffRelatedToMovie>>()
    val staffRelatedToMovieLiveData: LiveData<List<StaffRelatedToMovie>> get() = _staffRelatedToMovieLiveData

    private val _galleryPreviewLiveData = MutableLiveData<List<GalleryImage>>()
    val galleryPreviewLiveData: LiveData<List<GalleryImage>> get() = _galleryPreviewLiveData

    private val _similarMoviesLiveData = MutableLiveData<List<MovieSimilar>>()
    val similarMoviesLiveData: LiveData<List<MovieSimilar>> get() = _similarMoviesLiveData

    private val _numberOfImagesLiveData = MutableLiveData<Int>()
    val numberOfImagesLiveData: LiveData<Int> get() = _numberOfImagesLiveData

    private val _imageTypesAndItsQtyLiveData = MutableLiveData<Map<String, Int>>()
    val imageTypesAndItsQtyLiveData: LiveData<Map<String, Int>> get() = _imageTypesAndItsQtyLiveData

    private val _seriesLiveData = MutableLiveData<Series>()
    val seriesLiveData: LiveData<Series> get() = _seriesLiveData

    val collectionsLiveData = getUserCollectionsUseCase.execute().asLiveData()

    fun getMovieInfoByMovieId(movieId: Int) {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val movie = getMovieFullInfoByIdUseCase.execute(movieId)
                _movieLiveData.value = movie
            }
        }
    }

    fun getStaffByMovieId(movieId: Int) {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val staffList = getStaffListByMovieIdUseCase.execute(movieId)
                _staffRelatedToMovieLiveData.value = staffList
            }
        }
    }

    fun getGalleryInfoForPreview(movieId: Int) {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val imagesStill = getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, STILL)
                val imagesShooting = getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, SHOOTING)
                val imagesPoster = getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, POSTER)
                val imagesFanArt = getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, FAN_ART)
                val imagesPromo = getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, PROMO)
                val imagesConcept = getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, CONCEPT)
                val imagesWallpaper =
                    getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, WALLPAPER)
                val imagesCover = getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, COVER)
                val imagesScreenshot =
                    getGalleryByMovieIdUseCase.execute(movieId, DEF_PAGE, SCREENSHOT)

                val resultMap = mutableMapOf<String, GalleryImageListPaged>()
                if (imagesStill.total != 0) resultMap[STILL] = imagesStill
                if (imagesShooting.total != 0) resultMap[SHOOTING] = imagesShooting
                if (imagesPoster.total != 0) resultMap[POSTER] = imagesPoster
                if (imagesFanArt.total != 0) resultMap[FAN_ART] = imagesFanArt
                if (imagesPromo.total != 0) resultMap[PROMO] = imagesPromo
                if (imagesConcept.total != 0) resultMap[CONCEPT] = imagesConcept
                if (imagesWallpaper.total != 0) resultMap[WALLPAPER] = imagesWallpaper
                if (imagesCover.total != 0) resultMap[COVER] = imagesCover
                if (imagesScreenshot.total != 0) resultMap[SCREENSHOT] = imagesScreenshot

                resultMap.values.forEach {
                    Log.d(APPLICATION_TAG, it.toString())
                }

                var generalSize = 0
                val listOfImageTypesQty = mutableListOf<Int>()
                val generalList = mutableListOf<GalleryImage>()
                val imageTypesAndItsQtyMap = mutableMapOf<String, Int>()
                resultMap.values.forEach { exactGalleryImageList ->
                    if (exactGalleryImageList.total != 0) {
                        generalSize += exactGalleryImageList.total
                        listOfImageTypesQty.add(exactGalleryImageList.total)
                        exactGalleryImageList.items.forEach { galleryImage ->
                            generalList.add(galleryImage)
                        }
                    }
                }
                _galleryPreviewLiveData.value =
                    if (generalList.size <= MAX_IMAGES_FOR_HORIZONTAL_PREVIEW)
                        generalList
                    else
                        generalList.subList(0, MAX_IMAGES_FOR_HORIZONTAL_PREVIEW)
                resultMap.forEach {
                    if (it.value.total != 0) {
                        imageTypesAndItsQtyMap[it.key] = it.value.total
                    }
                }
                _numberOfImagesLiveData.value = generalSize
                _imageTypesAndItsQtyLiveData.value = imageTypesAndItsQtyMap
            }
        }
    }

    fun getSimilarMoviesByMovieId(movieId: Int) {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val similarMoviesList = getSimilarMoviesByMovieIdUseCase.execute(movieId)
                _similarMoviesLiveData.value = similarMoviesList
            }
        }
    }

    fun getSeriesById(movieId: Int) {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val series = getSeriesByIdUseCase.execute(movieId)
                _seriesLiveData.value = series
            }
        }
    }

    fun addOrDeleteMovieToCollection(movie: MovieFullInfo, collectionId: Int) {
        viewModelScope.launch {
            if (collectionsLiveData.value!!.first { it.id == collectionId }.movies.any { it.id == movie.id }) {
                deleteMovieFromUserCollectionUseCase.execute(movie.id, collectionId)
            } else {
                addMovieToUserCollectionUseCase.execute(movie, collectionId)
            }
        }
    }

    fun addMovieToCollection(movie: MovieFullInfo, collectionId: Int) {
        viewModelScope.launch {
            addMovieToUserCollectionUseCase.execute(movie, collectionId)
        }
    }

    fun clearErrorState(){
        _connectionErrorState.value = false
        _otherErrorState.value = false
    }

    companion object {
        private const val DEF_PAGE = 1
        private const val STILL = "STILL"
        private const val SHOOTING = "SHOOTING"
        private const val POSTER = "POSTER"
        private const val FAN_ART = "FAN_ART"
        private const val PROMO = "PROMO"
        private const val CONCEPT = "CONCEPT"
        private const val WALLPAPER = "WALLPAPER"
        private const val COVER = "COVER"
        private const val SCREENSHOT = "SCREENSHOT"
    }
}

