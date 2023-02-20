package com.example.movies.presentation.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.entities.movie.MovieRelatedToStaff
import com.example.domain.entities.staff.StaffFullInfo
import com.example.movies.R
import com.example.movies.databinding.FragmentStaffBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.viewmodels.StaffViewModel
import com.example.movies.presentation.views.HorizontalMovieListView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaffFragment : Fragment() {

    private var staffId: Int? = null
    private var _binding: FragmentStaffBinding? = null
    private val binding get() = _binding!!
    private val vm: StaffViewModel by viewModels()
    private var staff: StaffFullInfo? = null
    private var watchedMovies: List<MovieGeneral>? = null
    private var bestMovies: List<MovieGeneral>? = null
    private var _bestMoviesView: HorizontalMovieListView? = null
    private val bestMoviesView get() = _bestMoviesView!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            staffId = it.getInt(ARG_STAFF_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(true){
                vm.clearErrorState()
                staffId?.let {
                    vm.getStaffFullInfoByStaffId(it)
                }
            }
        }
        vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(false){
                vm.clearErrorState()
                staffId?.let {
                    vm.getStaffFullInfoByStaffId(it)
                }
            }
        }
        _bestMoviesView = binding.staffBestMovies
        vm.watchedMovies.observe(viewLifecycleOwner) {
            this.watchedMovies = it
            setBestMovieListForPreview()
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        bestMoviesView.setName(getString(R.string.the_best))
        bestMoviesView.hideAllItemsButton()
        bestMoviesView.turnOnPlaceholders()
        vm.staffLiveData.observe(viewLifecycleOwner) { staffFullInfo ->
            staff = staffFullInfo
            bestMovies = getStaffBestMovies(staff!!)
            setBestMovieListForPreview()
            binding.apply {
                staffName.text = if (staffFullInfo.nameRu != null &&
                    staffFullInfo.nameRu.toString().isNotBlank()
                ) {
                    staffFullInfo.nameRu
                } else {
                    staffFullInfo.nameEn
                }
                staffProfession.text = staffFullInfo.profession
                val moviesCount = staffFullInfo.movies.distinctBy { it.id }.size
                filmographyCount.text =
                    resources.getQuantityString(R.plurals.count_movies, moviesCount, moviesCount)
                Glide
                    .with(staffPhoto)
                    .load(staffFullInfo.posterUrl)
                    .into(staffPhoto)
                if (bestMovies!!.isEmpty()) staffBestMovies.visibility = View.GONE
                staffBestMovies.setResourceOfAdditionalMovieInfo { movieId ->
                    vm.getMovieFullInfoSource(movieId)
                }
                staffBestMovies.setActionOnItemClick {
                    val bundle = Bundle().apply {
                        putInt(ARG_MOVIE_ID, it.id!!)
                    }
                    findNavController().navigate(
                        R.id.action_staffFragment_to_movieInfoFragment,
                        bundle
                    )
                }
            }
        }
        binding.toListButton.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable(ARG_STAFF_FULL_INFO, staff)
            }
            findNavController().navigate(R.id.action_staffFragment_to_filmographyFragment, bundle)
        }
        staffId?.let {
            vm.getStaffFullInfoByStaffId(it)
        }
    }

    private fun setBestMovieListForPreview() {
        when {
            bestMovies == null -> return
            watchedMovies == null -> bestMoviesView.setNewList(bestMovies!!)
            else -> {
                val filteredMovieList = bestMovies!!.map { movie ->
                    val isWatched = watchedMovies!!.firstOrNull { it.id == movie.id } != null
                    movie.also {
                        it.isWatched = isWatched
                    }
                }
                bestMoviesView.setNewList(filteredMovieList)
            }
        }
    }

    private fun getStaffBestMovies(staff: StaffFullInfo): List<MovieGeneral> {
        val bestMoviesRaw = staff.movies
            .filter { it.rating != null }
            .filter { !it.rating!!.contains("%") }
            .sortedBy { it.rating!!.toDouble() }
            .reversed()
        val bestMoviesMap = mutableMapOf<Int, MovieRelatedToStaff>()
        bestMoviesRaw.forEach { bestMoviesMap[it.id] = it }
        val bestMoviesFull = bestMoviesMap.values.toList()
        return if (bestMoviesFull.size > 10) {
            bestMoviesFull.subList(0, 10)
        } else {
            bestMoviesFull
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    companion object {
        private const val ARG_STAFF_ID = "staff_id"
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_STAFF_FULL_INFO = "staff_full_info"
    }
}