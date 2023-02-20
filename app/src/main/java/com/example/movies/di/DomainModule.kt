package com.example.movies.di

import com.example.domain.repositories.*
import com.example.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
class DomainModule {

    //GetPremieresListUseCase
    @Provides
    fun provideGetPremieresListUseCase(premiereRepository: PremiereRepository): GetPremieresListUseCase {
        return GetPremieresListUseCase(premiereRepository)
    }

    //GetMovieByIdUseCase
    @Provides
    fun provideGetMovieByIdUseCase(movieFullInfoRepository: MovieFullInfoRepository): GetMovieFullInfoByIdUseCase {
        return GetMovieFullInfoByIdUseCase(movieFullInfoRepository)
    }

    //GetStaffListByMovieIdUseCase
    @Provides
    fun provideGetStaffListByMovieIdUseCase(staffRelatedToMovieRepository: StaffRelatedToMovieRepository): GetStaffListByMovieIdUseCase {
        return GetStaffListByMovieIdUseCase(staffRelatedToMovieRepository)
    }

    //GetGalleryByMovieIdUseCase
    @Provides
    fun provideGetGalleryByMovieIdUseCase(galleryRepository: GalleryRepository): GetGalleryByMovieIdUseCase {
        return GetGalleryByMovieIdUseCase(galleryRepository)
    }

    //GetSimilarMoviesByMovieId
    @Provides
    fun provideGetSimilarMoviesByMovieId(similarMovieRepository: SimilarMovieRepository): GetSimilarMoviesByMovieIdUseCase {
        return GetSimilarMoviesByMovieIdUseCase(similarMovieRepository)
    }

    //GetStaffFullInfoByStaffIdUseCase
    @Provides
    fun provideGetStaffFullInfoByStaffIdUseCase(staffFullInfoRepository: StaffFullInfoRepository): GetStaffFullInfoByStaffIdUseCase {
        return GetStaffFullInfoByStaffIdUseCase(staffFullInfoRepository)
    }

    //GetGalleryStreamByMovieIdUseCase
    @Provides
    fun provideGetGalleryStreamByMovieIdUseCase(galleryRepository: GalleryRepository): GetGalleryStreamByMovieIdUseCase {
        return GetGalleryStreamByMovieIdUseCase(galleryRepository)
    }

    //GetSeriesByIdUseCase
    @Provides
    fun provideGetSeriesByIdUseCase(seriesRepository: SeriesRepository): GetSeriesByIdUseCase {
        return GetSeriesByIdUseCase(seriesRepository)
    }

    //GetMovieTopListByTypeUseCase
    @Provides
    fun provideGetMovieTopListByTypeUseCase(movieTopRepository: MovieTopRepository): GetMovieTopListByTypeUseCase {
        return GetMovieTopListByTypeUseCase(movieTopRepository)
    }

    //GetMovieTopListByTypeUseCase
    @Provides
    fun provideGetMovieListFilteredUseCase(movieFilteredRepository: MovieFilteredRepository): GetMovieListFilteredUseCase {
        return GetMovieListFilteredUseCase(movieFilteredRepository)
    }

    //GetGenresAndCountriesForFilteringUseCase
    @Provides
    fun provideGetGenresAndCountriesForFilteringUseCase(
        genresAndCountriesForFilteringRepository: GenresAndCountriesForFilteringRepository
    ): GetGenresAndCountriesForFilteringUseCase {
        return GetGenresAndCountriesForFilteringUseCase(genresAndCountriesForFilteringRepository)
    }

    //GetMovieTopListStreamByTypeUseCase
    @Provides
    fun provideGetMovieTopListStreamByTypeUseCase(movieTopRepository: MovieTopRepository): GetMovieTopListAsMovieGeneralListStreamUseCase {
        return GetMovieTopListAsMovieGeneralListStreamUseCase(movieTopRepository)
    }

    //GetMovieFilteredListAsMovieGeneralListStreamUseCase
    @Provides
    fun provideGetMovieFilteredListAsMovieGeneralListStreamUseCase(
        movieFilteredRepository: MovieFilteredRepository
    ): GetMovieFilteredListAsMovieGeneralListStreamUseCase {
        return GetMovieFilteredListAsMovieGeneralListStreamUseCase(movieFilteredRepository)
    }

    //GetUserCollectionsUseCase
    @Provides
    fun provideGetUserCollectionsUseCase(userCollectionsRepository: UserCollectionsRepository): GetUserCollectionsUseCase {
        return GetUserCollectionsUseCase(userCollectionsRepository)
    }

    //AddUserCollectionUseCase
    @Provides
    fun provideAddUserCollectionUseCase(userCollectionsRepository: UserCollectionsRepository): AddUserCollectionUseCase {
        return AddUserCollectionUseCase(userCollectionsRepository)
    }

    //AddMovieToUserCollectionUseCase
    @Provides
    fun provideAddMovieToUserCollectionUseCase(userCollectionsRepository: UserCollectionsRepository): AddMovieToUserCollectionUseCase {
        return AddMovieToUserCollectionUseCase(userCollectionsRepository)
    }

    //AddMovieToUserCollectionUseCase
    @Provides
    fun provideDeleteMovieToUserCollectionUseCase(userCollectionsRepository: UserCollectionsRepository): DeleteMovieFromUserCollectionUseCase {
        return DeleteMovieFromUserCollectionUseCase(userCollectionsRepository)
    }

    //GetIfShowOnboardingScreenUseCase
    @Provides
    fun provideGetIfShowOnboardingScreenUseCase(userCollectionsRepository: UserCollectionsRepository): GetIfShowOnboardingScreenUseCase {
        return GetIfShowOnboardingScreenUseCase(userCollectionsRepository)
    }

    //SubmitEntranceUseCase
    @Provides
    fun provideSubmitEntranceUseCase(userCollectionsRepository: UserCollectionsRepository): SubmitEntranceUseCase {
        return SubmitEntranceUseCase(userCollectionsRepository)
    }
}