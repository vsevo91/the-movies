package com.example.movies.presentation.fragments.home

import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.domain.entities.*
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneralList
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.domain.utilities.*
import com.example.movies.R
import com.example.movies.databinding.FragmentMovieInfoBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.fragments.dialogs.ThreeDotsBottomSheetDialogFragment
import com.example.movies.presentation.utilities.AppBarStateChangeListener
import com.example.movies.presentation.viewmodels.MovieInfoViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.R.attr
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieInfoFragment : Fragment() {

    private var movieId: Int? = null
    private var mMovie: MovieFullInfo? = null
    private var _binding: FragmentMovieInfoBinding? = null
    private val binding get() = _binding!!
    private val vm: MovieInfoViewModel by viewModels()
    private var descriptionTextExpanded = false
    private var listOfImagesForPreview: List<GalleryImage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movieId = it.getInt(ARG_MOVIE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(APPLICATION_TAG, "Movie ID: $movieId")
        _binding = FragmentMovieInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                doIfError(true) {
                    vm.clearErrorState()
                    startGettingAllInfo()
                }
            }
        }
        vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                doIfError(false) {
                    vm.clearErrorState()
                    startGettingAllInfo()
                }
            }
        }
        getAttrs(attr.colorPrimaryVariant) {
            binding.collapsingToolbarLayout.contentScrim = it.getDrawable(0)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.movieDescription.setOnClickListener {
            hideOrShowFullTextView(it as TextView)
        }
        val appBarStateChangeListener = createAppBarStateChangeListener()
        binding.appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener)
        vm.movieLiveData.observe(viewLifecycleOwner) { movie ->
            if (movie == null) {
                val message = getString(R.string.movie_was_not_found)
                (requireActivity() as MainActivity).showSnack(message)
                Log.d(APPLICATION_TAG, message)
                return@observe
            } else {
                mMovie = movie
                vm.addMovieToCollection(mMovie!!, WAS_INTERESTING_COLLECTION_ID)
                setAllViewsInHeader(movie)
            }
        }
        setViewOfInfoAboutActorStaff()
        setViewOfInfoAboutOtherStaff()
        startObservingDataInfoAboutAllStaff()
        setGalleryView()
        setViewOfInfoAboutSimilarMovies()
        setViewOfInfoAboutSeries()
        startGettingAllInfo()
    }


    private fun checkIfMovieIsInFavoriteList(userCollections: List<UserCollection>): Boolean {
        return if (userCollections.isNotEmpty()) {
            userCollections.first { it.id == FAVORITE_COLLECTION_ID }.movies.any { it.id == movieId }
        } else {
            false
        }
    }

    private fun checkIfMovieIsInToWatchList(userCollections: List<UserCollection>): Boolean {
        return if (userCollections.isNotEmpty()) {
            userCollections.first { it.id == TO_WATCH_COLLECTION_ID }.movies.any { it.id == movieId }
        } else {
            false
        }
    }

    private fun checkIfMovieIsInWatchedList(userCollections: List<UserCollection>): Boolean {
        return if (userCollections.isNotEmpty()) {
            userCollections.first { it.id == WATCHED_COLLECTION_ID }.movies.any { it.id == movieId }
        } else {
            false
        }
    }

    private fun createAppBarStateChangeListener(): AppBarStateChangeListener {
        return object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                when (state) {
                    State.COLLAPSED -> {
                        mMovie?.let {
                            binding.collapsingToolbarLayout.title = defineName(it)
                            getAttrs(attr.colorOnPrimary) { attrs ->
                                binding.collapsingToolbarLayout.setCollapsedTitleTextColor(
                                    attrs.getColor(0, 0)
                                )
                            }
                        }
                        binding.toolbar.setNavigationIconTint(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }
                    else -> {
                        binding.collapsingToolbarLayout.title = null
                        binding.toolbar.setNavigationIconTint(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.dark_gray
                            )
                        )
                    }
                }
            }
        }
    }


    private fun setViewOfInfoAboutActorStaff() {
        binding.actorList.apply {
            setName(getString(R.string.actors))
            setActionOnItemClick { staff ->
                val bundle = Bundle().apply {
                    putInt(ARG_STAFF_ID, staff.staffId)
                }
                findNavController().navigate(R.id.action_movieInfoFragment_to_staffFragment, bundle)
            }
            setActionOnButtonClick { title, _ ->
                val bundle = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putInt(ARG_MOVIE_ID, movieId!!)
                    putBoolean(ARG_IS_ACTOR_LIST, true)
                }
                findNavController().navigate(
                    R.id.action_movieInfoFragment_to_staffListFragment,
                    bundle
                )
            }
        }
    }

    private fun setViewOfInfoAboutOtherStaff() {
        binding.otherStaffList.apply {
            setName(getString(R.string.other_staff))
            setSpanCount(2)
            setMaxStaffListSize(6)
            setActionOnItemClick { staff ->
                val bundle = Bundle().apply {
                    putInt(ARG_STAFF_ID, staff.staffId)
                }
                findNavController().navigate(R.id.action_movieInfoFragment_to_staffFragment, bundle)
            }
            setActionOnButtonClick { title, _ ->
                val bundle = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putInt(ARG_MOVIE_ID, movieId!!)
                    putBoolean(ARG_IS_ACTOR_LIST, false)
                }
                findNavController().navigate(
                    R.id.action_movieInfoFragment_to_staffListFragment,
                    bundle
                )
            }
        }
    }

    private fun startObservingDataInfoAboutAllStaff() {
        vm.staffRelatedToMovieLiveData.observe(viewLifecycleOwner) { staffList ->
            val actorList = staffList.filter { it.professionKey == StaffRelatedToMovie.ACTOR }
            val otherStaffListRelatedToMovie =
                staffList.filter { it.professionKey != StaffRelatedToMovie.ACTOR }
            Log.d(APPLICATION_TAG, "Actors: $actorList")
            Log.d(APPLICATION_TAG, "Other staff: $otherStaffListRelatedToMovie")
            if (actorList.isEmpty())
                binding.actorList.visibility = View.GONE
            when (actorList.size) {
                1 -> binding.actorList.setSpanCount(1)
                2 -> binding.actorList.setSpanCount(2)
                3 -> binding.actorList.setSpanCount(3)
            }
            binding.actorList.setNewList(actorList)
            if (otherStaffListRelatedToMovie.isEmpty())
                binding.otherStaffList.visibility = View.GONE
            if (otherStaffListRelatedToMovie.size == 1) {
                binding.otherStaffList.setSpanCount(1)
            }
            binding.otherStaffList.setNewList(otherStaffListRelatedToMovie)
        }
    }

    private fun setGalleryView() {
        binding.horizontalGallery.apply {
            turnOnPlaceholders()
            setName(getString(R.string.gallery))
        }
        vm.apply {
            galleryPreviewLiveData.observe(viewLifecycleOwner) {
                if (it.isEmpty()) binding.horizontalGallery.visibility = View.GONE
                listOfImagesForPreview = it
                Log.d(
                    APPLICATION_TAG,
                    "Number of images for preview: ${listOfImagesForPreview?.size}"
                )
                binding.horizontalGallery.setNewList(it)
            }
            numberOfImagesLiveData.observe(viewLifecycleOwner) { totalNumberOfPictures ->
                binding.horizontalGallery.setNumberOfItemsAtButtonText(totalNumberOfPictures)
                binding.horizontalGallery.setActionOnItemClick { exactGalleryImage ->
                    val totalNumberForForPreview =
                        if (totalNumberOfPictures <= MAX_IMAGES_FOR_HORIZONTAL_PREVIEW)
                            totalNumberOfPictures
                        else
                            MAX_IMAGES_FOR_HORIZONTAL_PREVIEW
                    val galleryImageUrlList =
                        listOfImagesForPreview!!.map { it.imageUrl ?: "" }.toTypedArray()
                    val bundle = Bundle().apply {
                        putString(ARG_GALLERY_IMAGE, exactGalleryImage.imageUrl)
                        putInt(ARG_MOVIE_ID, movieId!!)
                        putInt(ARG_TOTAL_NUMBER_OF_IMAGES, totalNumberForForPreview)
                        putStringArray(ARG_GALLERY_IMAGE_LIST, galleryImageUrlList)
                    }
                    findNavController().navigate(
                        R.id.action_movieInfoFragment_to_galleryItemFullScreenFragment,
                        bundle
                    )
                }
            }
            imageTypesAndItsQtyLiveData.observe(viewLifecycleOwner) { imageTypesAndItsQtyMap ->
                binding.horizontalGallery.setActionOnButtonClick { title, _ ->
                    val bundle = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putInt(ARG_MOVIE_ID, movieId!!)
                        val arrayOfImageTypes = ArrayList(imageTypesAndItsQtyMap.keys)
                        val arrayOfImageTypesQty = ArrayList(imageTypesAndItsQtyMap.values)
                        putStringArrayList(ARG_IMAGE_TYPES_LIST, arrayOfImageTypes)
                        putIntegerArrayList(ARG_NUMBER_OF_IMAGE_TYPES_LIST, arrayOfImageTypesQty)
                    }
                    findNavController().navigate(
                        R.id.action_movieInfoFragment_to_galleryListFragment,
                        bundle
                    )
                }
            }
        }
    }

    private fun setViewOfInfoAboutSimilarMovies() {
        binding.similarMoviesList.apply {
            setName(getString(R.string.similar_movies))
            setActionOnButtonClick { title, listOfMovies ->
                val moviesListParcelable = MovieGeneralList(items = listOfMovies)
                val bundle = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putParcelable(ARG_MOVIES_LIST, moviesListParcelable)
                }
                findNavController().navigate(
                    R.id.action_movieInfoFragment_to_listPageFragment,
                    bundle
                )
            }
            setActionOnItemClick {
                val bundle = Bundle().apply {
                    putInt(ARG_MOVIE_ID, it.id!!)
                }
                findNavController().navigate(R.id.action_movieInfoFragment_self, bundle)
            }
        }
        vm.similarMoviesLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.similarMoviesList.visibility = View.GONE
            binding.similarMoviesList.setNewList(it)
        }
    }

    private fun setViewOfInfoAboutSeries() {
        vm.seriesLiveData.observe(viewLifecycleOwner) { series ->
            if (series.items.isNotEmpty()) {
                val numberOfSeasons = series.total
                val numberOfEpisodes = series.items.sumOf { it.episodes.size }
                binding.seasonsAndEpisodesCount.text = getString(
                    R.string.seasons_and_episodes_values,
                    resources.getQuantityString(
                        R.plurals.count_seasons,
                        numberOfSeasons,
                        numberOfSeasons
                    ),
                    resources.getQuantityString(
                        R.plurals.count_episodes,
                        numberOfEpisodes,
                        numberOfEpisodes
                    )
                )
                binding.toAllItemsButton.setOnClickListener {
                    val bundle = Bundle().apply {
                        val title = defineName(mMovie!!)
                        putString(ARG_TITLE, title)
                        putParcelable(ARG_SERIES, series)
                    }
                    findNavController().navigate(
                        R.id.action_movieInfoFragment_to_seriesInfoMainFragment,
                        bundle
                    )
                }
            }
        }
    }

    private fun startGettingAllInfo() {
        movieId?.let { movieId ->
            vm.apply {
                getMovieInfoByMovieId(movieId)
                getStaffByMovieId(movieId)
                getGalleryInfoForPreview(movieId)
                getSimilarMoviesByMovieId(movieId)
                getSeriesById(movieId)
            }
        }
    }

    private fun hideOrShowFullTextView(textView: TextView) {
        if (descriptionTextExpanded) {
            descriptionTextExpanded = false
            textView.maxLines = 7
        } else {
            descriptionTextExpanded = true
            textView.maxLines = Int.MAX_VALUE
        }
    }

    private fun getAttrs(attribute: Int, onUse: (TypedArray) -> Unit) {
        val attrs = intArrayOf(attribute)
        val thisAttrs = requireActivity().obtainStyledAttributes(attrs)
        onUse(thisAttrs)
        thisAttrs.recycle()
    }

    private fun setAllViewsInHeader(movie: MovieFullInfo) {
        binding.apply {
            movie.serial?.let {
                if (it) seriesInfoContainer.visibility = View.VISIBLE
            }
            if (movie.coverUrl == null) {
                movieNameTextView.text = defineName(movie)
            } else {
                movieNameTextView.text = null
                Glide
                    .with(logoImageView)
                    .load(movie.logoUrl)
                    .into(logoImageView)
                movieNameTextView.isGone = true
            }
            Glide
                .with(posterImage)
                .load(movie.coverUrl ?: movie.imageLarge)
                .into(posterImage)
            countryTextView.text = defineCountriesLengthAge(movie)
            yearTextView.text = defineYearAndGenres(movie)
            nameSmallTextView.text = defineNameAndRating(movie)
            if (movie.description != null && movie.description!!.isNotBlank()) {
                movieDescription.text = movie.description
            } else {
                movieDescription.visibility = View.GONE
            }
            threeDotsButton.setOnClickListener {
                mMovie?.let {
                    val bottomSheetDialog = ThreeDotsBottomSheetDialogFragment(it)
                    bottomSheetDialog.show(
                        parentFragmentManager,
                        ThreeDotsBottomSheetDialogFragment.DIALOG_TAG
                    )
                }
            }
            shareButton.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${movie.webUrl}")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
        vm.collectionsLiveData.observe(viewLifecycleOwner) { userCollections ->
            binding.likeButton.isSelected = checkIfMovieIsInFavoriteList(userCollections)
            binding.addToWatchButton.isSelected = checkIfMovieIsInToWatchList(userCollections)
            binding.addToWatchedButton.isSelected = checkIfMovieIsInWatchedList(userCollections)
            binding.likeButton.setOnClickListener {
                vm.addOrDeleteMovieToCollection(mMovie!!, FAVORITE_COLLECTION_ID)
            }
            binding.addToWatchButton.setOnClickListener {
                vm.addOrDeleteMovieToCollection(mMovie!!, TO_WATCH_COLLECTION_ID)
            }
            binding.addToWatchedButton.setOnClickListener {
                vm.addOrDeleteMovieToCollection(mMovie!!, WATCHED_COLLECTION_ID)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).turnOnStatusBarTransparency(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).turnOnStatusBarTransparency(binding.toolbar)
    }

//    private fun defineName(movie: MovieFullInfo): String {
//        return if (movie.nameRu != null) {
//            movie.nameRu!!
//        } else if (movie.nameEn != null) {
//            movie.nameEn!!
//        } else if (movie.nameOriginal != null) {
//            movie.nameOriginal!!
//        } else ""
//    }

    private fun defineYearAndGenres(movie: MovieFullInfo): String {
        val year = if (movie.year == null) {
            ""
        } else {
            "${movie.year}, "
        }
        return "$year${movie.genres?.joinToString(", ")}"
    }

    private fun defineNameAndRating(movie: MovieFullInfo): SpannableString {

        val rating = movie.rating ?: ""
        val name = defineName(movie)
        val ratingAndName = "$rating $name"
        val indexOfFirstSpace = ratingAndName.indexOf(' ')
        val spannableString = SpannableString(ratingAndName)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 0, indexOfFirstSpace, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        return spannableString
    }

    private fun defineName(movie: MovieFullInfo): String {
        val currentLocale = resources.configuration.locales[0].country
        return if (currentLocale == "RU" && movie.nameRu != null && movie.nameRu.toString()
                .isNotBlank()
        ) {
            movie.nameRu!!
        } else if (movie.nameEn != null && movie.nameEn.toString().isNotBlank()) {
            movie.nameEn!!
        } else if (movie.nameOriginal != null && movie.nameOriginal.toString().isNotBlank()) {
            movie.nameOriginal!!
        } else if (movie.nameRu != null && movie.nameRu.toString().isNotBlank()
        ) {
            movie.nameRu!!
        } else ""
    }

    private fun defineCountriesLengthAge(movie: MovieFullInfo): String {
        val movieLength = if (movie.filmLength != null) {
            val hours = movie.filmLength!!.toInt() / 60
            val minutes = movie.filmLength!!.toInt() % 60
            "${hours}${getString(R.string.hours)} ${minutes}${getString(R.string.minutes)}"
        } else {
            ""
        }
        val age = if (movie.ratingAgeLimits != null) {
            "${movie.ratingAgeLimits!!.substring(3)}+"
        } else {
            ""
        }

        val countries = movie.countries.joinToString(", ")
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            if (countries.isNotBlank()) {
                append(countries)
            }
            if (movieLength.isNotBlank()) {
                if (countries.isNotBlank()) append(", ")
                append(movieLength)
            }
            if (age.isNotBlank()) {
                append(", ")
                append(age)
            }
        }
        return stringBuilder.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_TITLE = "title"
        private const val ARG_MOVIES_LIST = "movies_list"
        private const val ARG_STAFF_ID = "staff_id"
        private const val ARG_IMAGE_TYPES_LIST = "list_of_image_types"
        private const val ARG_NUMBER_OF_IMAGE_TYPES_LIST = "number_of_each_image_type_list"
        private const val ARG_TOTAL_NUMBER_OF_IMAGES = "total_number_of_images"
        private const val ARG_GALLERY_IMAGE = "gallery_image"
        private const val ARG_GALLERY_IMAGE_LIST = "gallery_image_list"
        private const val ARG_SERIES = "series"
        private const val ARG_IS_ACTOR_LIST = "is_actors_list"
    }
}