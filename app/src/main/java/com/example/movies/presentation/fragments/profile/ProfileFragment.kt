package com.example.movies.presentation.fragments.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entities.UserCollection
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.WAS_INTERESTING_COLLECTION_ID
import com.example.domain.utilities.WATCHED_COLLECTION_ID
import com.example.movies.R
import com.example.movies.databinding.FragmentProfileBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForUserCollectionsList
import com.example.movies.presentation.fragments.dialogs.CreateUserCollectionDialogFragment
import com.example.movies.presentation.utilities.SpaceItemDecoration
import com.example.movies.presentation.viewmodels.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val vm: ProfileViewModel by viewModels()
    private var isNewCollectionJustAdded = false
    private var itemAnimator: RecyclerView.ItemAnimator? = null
    private var _recyclerView: RecyclerView? = null
    private val recyclerView get() = _recyclerView!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.watchedMoviesList.setName(getString(R.string.watched_movies))
        binding.watchedMoviesList.setActionOnButtonClick { _, _ ->
            val bundle = Bundle().apply {
                putInt(ARG_COLLECTION_ID, WATCHED_COLLECTION_ID)
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_collectionMovieListFragment,
                bundle
            )
        }
        binding.watchedMoviesList.setActionOnItemClick {
            val bundle = Bundle().apply {
                putInt(ARG_MOVIE_ID, it.id!!)
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_navigation,
                bundle
            )
        }
        binding.recentList.setName(getString(R.string.recent_movie_list))
        binding.recentList.setActionOnButtonClick { _, _ ->
            val bundle = Bundle().apply {
                putInt(ARG_COLLECTION_ID, WAS_INTERESTING_COLLECTION_ID)
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_collectionMovieListFragment,
                bundle
            )
        }
        binding.recentList.setActionOnItemClick {
            val bundle = Bundle().apply {
                putInt(ARG_MOVIE_ID, it.id!!)
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_navigation,
                bundle
            )
        }
        _recyclerView = binding.recyclerView
        itemAnimator = recyclerView.itemAnimator
        val adapter = AdapterForUserCollectionsList()
        recyclerView.adapter = adapter
        allowRecyclerViewToAnimateChanges(false)
        recyclerView.addItemDecoration(SpaceItemDecoration(20, 40))
        adapter.setOnDeleteButtonClick {
            allowRecyclerViewToAnimateChanges(true)
            vm.deleteCollection(it)
        }
        vm.collectionsFlow.observe(viewLifecycleOwner) { userCollections ->
            Log.d(APPLICATION_TAG, "${userCollections.map { "${it.name}: ${it.movies.size}" }}")
            val filteredCollections = removeHiddenCollections(userCollections)
            adapter.submitList(filteredCollections)
            scrollRecyclerViewIfCollectionIsAdded()
            val watchedMoviesList =
                userCollections.firstOrNull { it.id == WATCHED_COLLECTION_ID }?.movies?.map {
                    it.apply{it.isWatched = true}
                }
            val watchedMoviesListSorted =
                watchedMoviesList?.sortedBy { it.addingTime }?.reversed() ?: emptyList()
            if (watchedMoviesListSorted.isEmpty()) {
                binding.watchedMoviesList.enableEmptyCollectionMessage()
            } else {
                binding.watchedMoviesList.disableEmptyCollectionMessage()
            }
            binding.watchedMoviesList.setNewList(watchedMoviesListSorted)
            val recentMoviesList =
                userCollections.firstOrNull { it.id == WAS_INTERESTING_COLLECTION_ID }?.movies
            val recentMoviesListSorted =
                recentMoviesList?.sortedBy { it.addingTime }?.reversed()?.map {movie ->
                    val isWatched = watchedMoviesList?.firstOrNull { it.id == movie.id } != null
                    movie.also {
                        it.isWatched = isWatched
                    }
                } ?: emptyList()
            if (recentMoviesListSorted.isEmpty()) {
                binding.recentList.enableEmptyCollectionMessage()
            } else {
                binding.recentList.disableEmptyCollectionMessage()
            }
            binding.recentList.setNewList(recentMoviesListSorted)
        }

        binding.addCollectionButtonLayout.setOnClickListener {
            allowRecyclerViewToAnimateChanges(true)
            showCollectionCreationDialog()
        }
        adapter.setOnItemClick { collection ->
            val bundle = Bundle().apply {
                putInt(ARG_COLLECTION_ID, collection.id!!)
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_collectionMovieListFragment,
                bundle
            )
        }
    }

    override fun onPause() {
        super.onPause()
        allowRecyclerViewToAnimateChanges(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun removeHiddenCollections(userCollections: List<UserCollection>): List<UserCollection> {
        return userCollections.filter { !it.isHidden }
    }

    private fun showCollectionCreationDialog() {
        val dialogFragment = CreateUserCollectionDialogFragment()
        dialogFragment.setOnReadyButtonClickListener { collectionName ->
            if (collectionName.isNotBlank()) {
                val userCollection = UserCollection(
                    name = collectionName,
                    icon = R.drawable.ic_profile,
                    movies = emptyList()
                )
                vm.addCollection(userCollection)
                isNewCollectionJustAdded = true
                dialogFragment.dialog!!.cancel()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.enter_acceptable_name),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        dialogFragment.show(parentFragmentManager, CreateUserCollectionDialogFragment.DIALOG_TAG)
    }

    private fun scrollRecyclerViewIfCollectionIsAdded() {
        if (isNewCollectionJustAdded) {
            val position = binding.recyclerView.adapter?.itemCount
            position?.let { binding.recyclerView.smoothScrollToPosition(it) }
            isNewCollectionJustAdded = false
        }
    }

    private fun allowRecyclerViewToAnimateChanges(isAllowed: Boolean) {
        try {
            if (isAllowed) {
                recyclerView.itemAnimator = itemAnimator
                Log.d(APPLICATION_TAG, "Recycler View Animation is enabled")
            } else {
                recyclerView.itemAnimator = null
                Log.d(APPLICATION_TAG, "Recycler View Animation is disabled")
            }
        } catch (t: Throwable) {
            Log.d(APPLICATION_TAG, "${t.message}")
        }
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