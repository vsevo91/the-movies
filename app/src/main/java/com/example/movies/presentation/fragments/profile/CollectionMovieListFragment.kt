package com.example.movies.presentation.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.utilities.WATCHED_COLLECTION_ID
import com.example.movies.R
import com.example.movies.databinding.FragmentCollectionMovieListBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForVerticalMovieListInCollection
import com.example.movies.presentation.utilities.SpaceItemDecoration
import com.example.movies.presentation.viewmodels.CollectionMovieListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionMovieListFragment : Fragment() {

    private var collectionId: Int? = null
    private var param2: String? = null
    private var _binding: FragmentCollectionMovieListBinding? = null
    private val binding get() = _binding!!
    private val vm: CollectionMovieListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            collectionId = it.getInt(ARG_COLLECTION_ID)
            param2 = it.getString(ARG_MOVIE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionMovieListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        if (collectionId == null) return
        val adapter = AdapterForVerticalMovieListInCollection()
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpaceItemDecoration(0, 20))
        vm.moviesFromCollection.observe(viewLifecycleOwner) { collections ->
            val watchedMovies = collections.firstOrNull { it.id == WATCHED_COLLECTION_ID }?.movies

            val currentCollection = collections.find { it.id == collectionId }
            binding.toolbarTitle.text = currentCollection?.name
            val movies = currentCollection?.movies?.map { movie ->
                val isWatched = watchedMovies?.firstOrNull { it.id == movie.id } != null
                movie.also {
                    it.isWatched = isWatched
                }
            }
            movies?.let { moviesNotNull ->
                val sortedMovieList =
                    moviesNotNull.sortedBy { it.addingTime }.reversed()
                if (sortedMovieList.isEmpty()) {
                    binding.emptyCollectionText.visibility = View.VISIBLE
                } else {
                    binding.emptyCollectionText.visibility = View.INVISIBLE
                }
                adapter.submitList(sortedMovieList)
            }
        }
        vm.titleLiveData.observe(viewLifecycleOwner) { title ->
            binding.toolbarTitle.text = title
        }
        adapter.setOnDeleteButtonClickListener { movie ->
            vm.deleteMovieFromUserCollection(movie.id!!, collectionId!!)
        }
        adapter.setItemClickListener {
            val bundle = Bundle().apply {
                putInt(ARG_MOVIE_ID, it.id!!)
            }
            findNavController().navigate(
                R.id.action_collectionMovieListFragment_to_navigation,
                bundle
            )
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
        private const val ARG_COLLECTION_ID = "collection_id"
        private const val ARG_MOVIE_ID = "movie_id"
    }
}







