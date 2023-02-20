package com.example.movies.di

import android.content.Context
import com.example.data.repositories.*
import com.example.data.storages.*
import com.example.data.storages.apistorage.filteringstorage.ApiGenresAndCountriesForFilteringStorageImpl
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.gallerystorage.ApiGalleryStorageImpl
import com.example.data.storages.apistorage.moviesstorage.*
import com.example.data.storages.apistorage.seriesstorage.ApiSeriesStorageImpl
import com.example.data.storages.apistorage.staffstorage.ApiStaffFullInfoStorageImpl
import com.example.data.storages.apistorage.staffstorage.ApiStaffRelatedToMovieStorageImpl
import com.example.data.storages.databasestorage.AppDatabase
import com.example.data.storages.databasestorage.DbUserCollectionStorageImpl
import com.example.data.storages.databasestorage.UserCollectionDao
import com.example.domain.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    //Retrofit
    @Singleton
    @Provides
    fun providesKinopoiskService(): KinopoiskService {
        return KinopoiskService.create()
    }


    //ApiPremieres_Storage
    @Singleton
    @Provides
    fun providesApiPremieresStorage(kinopoiskService: KinopoiskService): ApiPremieresStorage {
        return ApiPremieresStorageImpl(kinopoiskService)
    }

    //Premieres_Repository
    @Singleton
    @Provides
    fun providesPremiereRepository(apiPremieresStorage: ApiPremieresStorage): PremiereRepository {
        return PremiereRepositoryImpl(apiPremieresStorage)
    }

    //ApiMovies_Storage
    @Singleton
    @Provides
    fun providesApiMoviesStorage(kinopoiskService: KinopoiskService): ApiMovieFullInfoStorage {
        return ApiMovieFullInfoStorageImpl(kinopoiskService)
    }

    //Movies_Repository
    @Singleton
    @Provides
    fun providesMovieRepository(apiMovieFullInfoStorage: ApiMovieFullInfoStorage): MovieFullInfoRepository {
        return MovieFullInfoRepositoryImpl(apiMovieFullInfoStorage)
    }

    //ApiStaff_Storage
    @Singleton
    @Provides
    fun providesApiStaffStorage(kinopoiskService: KinopoiskService): ApiStaffRelatedToMovieStorage {
        return ApiStaffRelatedToMovieStorageImpl(kinopoiskService)
    }

    //Staff_Repository
    @Singleton
    @Provides
    fun providesStaffRepository(apiStaffRelatedToMovieStorage: ApiStaffRelatedToMovieStorage): StaffRelatedToMovieRepository {
        return StaffRelatedToMovieRepositoryImpl(apiStaffRelatedToMovieStorage)
    }

    //ApiGallery_Storage
    @Singleton
    @Provides
    fun providesApiGalleryStorage(kinopoiskService: KinopoiskService): ApiGalleryStorage {
        return ApiGalleryStorageImpl(kinopoiskService)
    }

    //Gallery_Repository
    @Singleton
    @Provides
    fun providesGalleryRepository(apiGalleryStorage: ApiGalleryStorage): GalleryRepository {
        return GalleryRepositoryImpl(apiGalleryStorage)
    }

    //MovieSimilar_Storage
    @Singleton
    @Provides
    fun provideApiSimilarMoviesStorage(kinopoiskService: KinopoiskService): ApiSimilarMoviesStorage {
        return ApiSimilarMoviesStorageImpl(kinopoiskService)
    }

    //MovieSimilar_Repository
    @Singleton
    @Provides
    fun provideSimilarMoviesRepository(apiSimilarMoviesStorage: ApiSimilarMoviesStorage): SimilarMovieRepository {
        return SimilarMovieRepositoryImpl(apiSimilarMoviesStorage)
    }

    //StaffFullInfo_Storage
    @Singleton
    @Provides
    fun provideApiStaffFullInfoStorage(kinopoiskService: KinopoiskService): ApiStaffFullInfoStorage {
        return ApiStaffFullInfoStorageImpl(kinopoiskService)
    }

    //StaffFullInfo_Repository
    @Singleton
    @Provides
    fun provideStaffFullInfoRepository(apiStaffFullInfoStorage: ApiStaffFullInfoStorage): StaffFullInfoRepository {
        return StaffFullInfoRepositoryImpl(apiStaffFullInfoStorage)
    }

    //Series_Storage
    @Singleton
    @Provides
    fun provideApiSeriesStorage(kinopoiskService: KinopoiskService): ApiSeriesStorage {
        return ApiSeriesStorageImpl(kinopoiskService)
    }

    //Series_Repository
    @Singleton
    @Provides
    fun provideSeriesRepository(apiSeriesStorage: ApiSeriesStorage): SeriesRepository {
        return SeriesRepositoryImpl(apiSeriesStorage)
    }

    //MovieTop_Storage
    @Singleton
    @Provides
    fun provideApiMovieTopStorage(kinopoiskService: KinopoiskService): ApiMovieTopStorage {
        return ApiMovieTopStorageImpl(kinopoiskService)
    }

    //MovieTop_Repository
    @Singleton
    @Provides
    fun provideMovieTopRepository(apiMovieTopStorage: ApiMovieTopStorage): MovieTopRepository {
        return MovieTopRepositoryImpl(apiMovieTopStorage)
    }

    //MovieFiltered_Storage
    @Singleton
    @Provides
    fun provideApiMovieFilteredStorage(kinopoiskService: KinopoiskService): ApiMovieFilteredStorage {
        return ApiMovieFilteredStorageImpl(kinopoiskService)
    }

    //MovieFiltered_Repository
    @Singleton
    @Provides
    fun provideMovieFilteredRepository(apiMovieFilteredStorage: ApiMovieFilteredStorage): MovieFilteredRepository {
        return MovieFilteredRepositoryImpl(apiMovieFilteredStorage)
    }

    //GenresAndCountriesForFiltering_Storage
    @Singleton
    @Provides
    fun provideApiGenresAndCountriesForFilteringStorage(kinopoiskService: KinopoiskService): ApiGenresAndCountriesForFilteringStorage {
        return ApiGenresAndCountriesForFilteringStorageImpl(kinopoiskService)
    }

    //GenresAndCountriesForFiltering_Repository
    @Singleton
    @Provides
    fun provideGenresAndCountriesForFilteringRepository(
        apiGenresAndCountriesForFilteringStorage: ApiGenresAndCountriesForFilteringStorage
    ): GenresAndCountriesForFilteringRepository {
        return GenresAndCountriesForFilteringRepositoryImpl(apiGenresAndCountriesForFilteringStorage)
    }

    //Database
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    //UserCollectionDao
    @Singleton
    @Provides
    fun provideUserCollectionDao(appDatabase: AppDatabase): UserCollectionDao {
        return appDatabase.userCollectionDao()
    }

    //UserCollections_Storage
    @Singleton
    @Provides
    fun provideDbUserCollectionStorageImpl(userCollectionDao: UserCollectionDao): DbUserCollectionStorage {
        return DbUserCollectionStorageImpl(userCollectionDao)
    }

    //UserCollections_Repository
    @Singleton
    @Provides
    fun provideUserCollectionsRepository(dbUserCollectionStorage: DbUserCollectionStorage): UserCollectionsRepository {
        return UserCollectionsRepositoryImpl(dbUserCollectionStorage)
    }
}






