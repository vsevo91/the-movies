package com.example.movies.presentation.fragments.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.entities.movie.MovieGeneralList
import com.example.movies.R
import com.example.movies.databinding.FragmentStaffFilmographyItemBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForVerticalMovieList
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.utilities.SpaceItemDecoration
import com.example.movies.presentation.viewmodels.StaffFilmographyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaffFilmographyItemFragment : Fragment() {

    private var movieList: MovieGeneralList? = null
    private var _binding: FragmentStaffFilmographyItemBinding? = null
    private val binding get() = _binding!!
    private val vm: StaffFilmographyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT < 33) {
                @Suppress("DEPRECATION")
                movieList = it.getParcelable(ARG_MOVIE_LIST)
            } else {
                movieList = it.getParcelable(ARG_MOVIE_LIST, MovieGeneralList::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffFilmographyItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(true) {
                vm.clearErrorState()
            }
        }
        vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(false) {
                vm.clearErrorState()
            }
        }
        val currentLocale = resources.configuration.locales[0].country
        val adapter =
            AdapterForVerticalMovieList(
                currentLocale,
                movieList?.items?.sortedBy { it.rating }?.reversed()
                    ?: emptyList()
            )
        adapter.setResourceOfAdditionalMovieInfoIfNeeded { movieId ->
            vm.getMovieFullInfoByMovieId(movieId)
        }
        adapter.setItemClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_MOVIE_ID, it.id!!)
            }
            findNavController().navigate(
                R.id.action_filmographyFragment_to_movieInfoFragment,
                bundle
            )
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(0, 20))

        vm.watchedMovies.observe(viewLifecycleOwner) { watchedMovies ->
            val checkedMovies = movieList!!.items.map { movie ->
                val isWatched = watchedMovies?.firstOrNull { it.id == movie.id } != null
                movie.also {
                    it.isWatched = isWatched
                }
            }
            adapter.submitNewList(checkedMovies.sortedBy { it.rating }.reversed())
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
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_MOVIE_LIST = "staff_movie_list"
    }
}