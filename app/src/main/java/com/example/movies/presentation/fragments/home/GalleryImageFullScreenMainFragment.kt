package com.example.movies.presentation.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.databinding.FragmentGalleryImageFullScreenMainBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.GalleryFullScreenFragmentStateAdapter
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.viewmodels.GalleryImageFullScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryImageFullScreenMainFragment : Fragment() {

    private var imageType: String? = null
    private var totalNumberOfImages: Int? = null
    private var movieId: Int? = null
    private var startGalleryImageList: Array<String>? = null
    private var currentGalleryImageList: Array<String>? = null
    private var currentPage: Int? = null
    private var startGalleryImage: String? = null
    private var pager: ViewPager2? = null
    private var adapter: GalleryFullScreenFragmentStateAdapter? = null
    private var _binding: FragmentGalleryImageFullScreenMainBinding? = null
    private val binding get() = _binding!!
    private val vm: GalleryImageFullScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(APPLICATION_TAG, "CurrentPage: $currentPage")
        arguments?.let {
            imageType = it.getString(ARG_IMAGE_TYPE)
            totalNumberOfImages = it.getInt(ARG_TOTAL_NUMBER_OF_IMAGES)
            movieId = it.getInt(ARG_MOVIE_ID)
            startGalleryImageList = it.getStringArray(ARG_GALLERY_IMAGE_LIST)
            startGalleryImage = it.getString(ARG_GALLERY_IMAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(APPLICATION_TAG, "CurrentPage: $currentPage")
        arguments?.let {
            currentGalleryImageList = it.getStringArray(ARG_CURRENT_LIST)
            currentPage = it.getInt(ARG_CURRENT_PAGE)
        }
        Log.d(APPLICATION_TAG, "Instance was restored")
        Log.d(APPLICATION_TAG, "Current list size: ${currentGalleryImageList?.size}")
        _binding = FragmentGalleryImageFullScreenMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        arguments?.let {
            adapter?.let { adapter ->
                val arrayList: Array<String> = adapter.getItemList().toTypedArray()
                it.putStringArray(ARG_CURRENT_LIST, arrayList)
                it.putInt(ARG_CURRENT_PAGE, currentPage!!)
                Log.d(APPLICATION_TAG, "Instance was saved")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(true) {
                vm.clearErrorState()
                adapter?.reconnect()
            }
        }
        vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(false) {
                vm.clearErrorState()
                adapter?.reconnect()
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        pager = binding.viewPager
        adapter =
            GalleryFullScreenFragmentStateAdapter(
                this,
                currentList = currentGalleryImageList?.toList() ?: startGalleryImageList!!.toList(),
                totalNumberOfImages = totalNumberOfImages!!,
                downloadNextPageData = { page ->
                    if (imageType != null) {
                        vm.loadImagesByTypeAndPage(movieId!!, page, imageType!!)
                    } else null
                }
            )
        pager?.adapter = adapter
        pager?.offscreenPageLimit = 3
        val startPosition = startGalleryImageList!!.indexOf(startGalleryImage!!)
        pager?.setCurrentItem(startPosition, false)
        Log.d(APPLICATION_TAG, "CurrentPage: $currentPage")
        changeTitle(currentPage = if (currentPage == 0) startPosition + 1 else currentPage!!)
        pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentPage = position + 1
                this@GalleryImageFullScreenMainFragment.currentPage = currentPage
                changeTitle(currentPage)
                Log.d(APPLICATION_TAG, "Current page: $currentPage")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    private fun changeTitle(currentPage: Int) {
        binding.toolbar.title = "$currentPage/$totalNumberOfImages"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_TYPE = "image_type"
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_GALLERY_IMAGE_LIST = "gallery_image_list"
        private const val ARG_GALLERY_IMAGE = "gallery_image"
        private const val ARG_TOTAL_NUMBER_OF_IMAGES = "total_number_of_images"
        private const val ARG_CURRENT_LIST = "current_list"
        private const val ARG_CURRENT_PAGE = "current_page"
    }
}