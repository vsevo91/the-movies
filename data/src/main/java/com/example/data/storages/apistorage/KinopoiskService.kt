package com.example.data.storages.apistorage

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskService {
    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films/premieres")
    suspend fun getPremieres(
        @Query("year") year: Int,
        @Query("month") month: String
    ): Response<com.example.data.storages.apistorage.entities.movie.PremiereListResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int,
    ): Response<com.example.data.storages.apistorage.entities.movie.MovieFullInfoResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v1/staff")
    suspend fun getStaffById(
        @Query("filmId") filmId: Int,
    ): Response<List<com.example.data.storages.apistorage.entities.staff.StaffRelatedToMovieResponse>>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films/{movieId}/images")
    suspend fun getGalleryByIdPaged(
        @Path("movieId") movieId: Int,
        @Query("page") page: Int,
        @Query("type") type: String = IMAGE_TYPE_DEFAULT
    ): Response<com.example.data.storages.apistorage.entities.gallery.GalleryImageListResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films/{movieId}/similars")
    suspend fun getSimilarMoviesById(
        @Path("movieId") movieId: Int
    ): Response<com.example.data.storages.apistorage.entities.movie.SimilarMovieListResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v1/staff/{staffId}")
    suspend fun getStaffFullInfoById(
        @Path("staffId") staffId: Int
    ): Response<com.example.data.storages.apistorage.entities.staff.StaffFullInfoResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films/{movieId}/seasons")
    suspend fun getSeriesById(
        @Path("movieId") movieId: Int
    ): Response<com.example.data.storages.apistorage.entities.series.SeriesResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films/top")
    suspend fun getMovieTopByCategoryPaged(
        @Query("type") type: String,
        @Query("page") page: Int
    ): Response<com.example.data.storages.apistorage.entities.movie.MovieTopListResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films")
    suspend fun getMovieListFilteredPaged(
        @Query("countries") countries: Array<Int>?,
        @Query("genres") genres: Array<Int>?,
        @Query("order") order: String,
        @Query("type") type: String,
        @Query("ratingFrom") ratingFrom: Int,
        @Query("ratingTo") ratingTo: Int,
        @Query("yearFrom") yearFrom: Int,
        @Query("yearTo") yearTo: Int,
        @Query("keyword") keyword: String,
        @Query("page") page: Int
    ): Response<com.example.data.storages.apistorage.entities.movie.MovieByFiltersListResponse>

    @Headers("X-API-KEY: $X_API_KEY_2")
    @GET("/api/v2.2/films/filters")
    suspend fun getGenresAndCountriesForFiltering(): Response<com.example.data.storages.apistorage.entities.filtering.GenresAndCountriesForFilteringResponse>

    companion object {
        //        private const val X_API_KEY_1 = "4b97446c-10e3-4762-9e1f-df14572ee0db"
        private const val X_API_KEY_2 = "4851ffd1-f2a3-4eb8-8891-cb5b20815e98"
        private const val BASE_API = "https://kinopoiskapiunofficial.tech"
        private const val IMAGE_TYPE_DEFAULT = "STILL"

        fun create(): KinopoiskService {
            return Retrofit.Builder()
                .baseUrl(BASE_API)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(KinopoiskService::class.java)
        }
    }
}