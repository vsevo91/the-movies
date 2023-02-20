package com.example.movies.presentation.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.FragmentGalleryListItemBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForGalleryListPaged
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.utilities.SpaceItemDecoration
import com.example.movies.presentation.viewmodels.GalleryListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryListItemFragment : Fragment() {

    private var imageType: String? = null
    private var totalNumberOfImages: Int? = null
    private var movieId: Int? = null
    private var _binding: FragmentGalleryListItemBinding? = null
    private val binding get() = _binding!!
    private var _adapter: AdapterForGalleryListPaged? = null
    private val adapter get() = _adapter!!
    private val vm: GalleryListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageType = it.getString(ARG_IMAGE_TYPE)
            totalNumberOfImages = it.getInt(ARG_TOTAL_NUMBER_OF_IMAGES)
            movieId = it.getInt(ARG_MOVIE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryListItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _adapter = AdapterForGalleryListPaged { exactGalleryImage ->
            val galleryImageUrlList =
                adapter.snapshot().items.map { it.imageUrl ?: "" }.toTypedArray()
            val bundle = Bundle().apply {
                putString(ARG_GALLERY_IMAGE, exactGalleryImage.imageUrl)
                putInt(ARG_MOVIE_ID, movieId!!)
                putString(ARG_IMAGE_TYPE, imageType)
                putInt(ARG_TOTAL_NUMBER_OF_IMAGES, totalNumberOfImages!!)
                putStringArray(ARG_GALLERY_IMAGE_LIST, galleryImageUrlList)
            }
            findNavController().navigate(
                R.id.action_galleryListFragment_to_galleryItemFullScreenFragment, bundle
            )
        }
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(10, 20))
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.getStream(movieId!!, imageType!!).collect {
                    adapter.submitData(it)
                }
            }
        }
        adapter.loadStateFlow.onEach {

            if (it.source.refresh is LoadState.Error ||
                it.source.append is LoadState.Error ||
                it.source.prepend is LoadState.Error
            ) {
                Log.d(APPLICATION_TAG, "Load state: ${it.source.append}")
                if (it.source.append.toString() ==
                    LoadState.Error(Throwable(ONLY_FIRST_400_STATE)).toString()) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.only_first_400_images_are_available),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    doIfError(true) {
                        adapter.retry()
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_TYPE = "image_type"
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_GALLERY_IMAGE = "gallery_image"
        private const val ARG_GALLERY_IMAGE_LIST = "gallery_image_list"
        private const val ARG_TOTAL_NUMBER_OF_IMAGES = "total_number_of_images"
        private const val ONLY_FIRST_400_STATE = "only_first_400_images_state"
    }
}