package com.example.movies.presentation.fragments.search


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.FragmentSearchBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForVerticalMovieListPaged
import com.example.movies.presentation.adapters.SearchLoadStateAdapter
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.utilities.SpaceItemDecoration
import com.example.movies.presentation.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null
    private val vm: SearchViewModel by activityViewModels()
    private var _adapter: AdapterForVerticalMovieListPaged? = null
    private val adapter get() = _adapter!!
    private var isScrollingUpEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(true){
                vm.clearErrorState()
                vm.getGenresAndCountriesForFiltering()
            }
        }
        vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(false){
                vm.clearErrorState()
                vm.getGenresAndCountriesForFiltering()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.isScrollingUpEnabled.collect {
                    isScrollingUpEnabled = it
                }
            }
        }
        _adapter = AdapterForVerticalMovieListPaged()
        binding.recyclerView.adapter = adapter.withLoadStateFooter(SearchLoadStateAdapter(
            onRetry = {adapter.retry()}
        ))
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(0, 20))
        adapter.loadStateFlow.onEach {
            if (it.refresh == LoadState.Loading) {
                binding.progressBar.isVisible = true
                binding.nothingFoundMessage.isVisible = false
                Log.d(APPLICATION_TAG, "REFRESH LoadState:${it.refresh}")
            } else {
                Log.d(APPLICATION_TAG, "Scrolling state: $isScrollingUpEnabled")
                binding.progressBar.isVisible = false
                if (isScrollingUpEnabled) {
                    binding.recyclerView.scrollToPosition(0)
                }
                if (adapter.itemCount == 0) {
                    binding.nothingFoundMessage.isVisible = true
                    Log.d(APPLICATION_TAG, "Nothing found")
                }
            }
            if (it.append == LoadState.Loading) {
                vm.disableScrollingUp()
                Log.d(APPLICATION_TAG, "APPEND LoadState: ${it.append}")
            }
            if(it.source.refresh is LoadState.Error){
                doIfError(true){
                    adapter.retry()
                }
            }
        }.launchIn(lifecycleScope)
        adapter.setItemClickListener { movieGeneral ->
            val id = movieGeneral.id
            val bundle = Bundle().apply {
                putInt(ARG_MOVIE_ID, id!!)
            }
            findNavController().navigate(R.id.action_searchFragment_to_navigation, bundle)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                vm.filteredStreamOdMovies.collect { pagingData ->
                    job?.cancel()
                    job = null
                    job = viewLifecycleOwner.lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            pagingData.collectLatest { movieGeneralPagingData ->
                                Log.d(APPLICATION_TAG, "Response was received")
                                adapter.submitData(movieGeneralPagingData)
                            }
                        }
                    }
                }
            }
        }


        vm.tryToGetMovieFilteredStreamWithDelay()
        binding.searchBar.setOnFocusChangeListener { _, inFocus ->
            if (inFocus) {
                binding.searchBar.addTextChangedListener { searchText ->
                    vm.enableSearching()
                    vm.enableScrollingUp()
                    Log.d(APPLICATION_TAG, "TEXT CHANGED")
                    vm.changeSearchSettingsKeyword(searchText.toString())
                    vm.tryToGetMovieFilteredStreamWithDelay()
                }
            }
        }
        binding.endIconButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchSettingsFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        vm.disableSearching()
        vm.disableScrollingUp()
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
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}