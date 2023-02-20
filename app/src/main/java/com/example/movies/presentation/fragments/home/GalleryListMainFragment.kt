package com.example.movies.presentation.fragments.home

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.movies.R
import com.example.movies.databinding.FragmentGalleryListMainBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.GalleryListFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryListMainFragment : Fragment() {

    private var title: String? = null
    private var listOfImageTypes: ArrayList<String>? = null
    private var listOfImageTypesQty: ArrayList<Int>? = null
    private var movieId: Int? = null
    private var _binding: FragmentGalleryListMainBinding? = null
    private val binding get() = _binding!!
    private var adapter: GalleryListFragmentStateAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            listOfImageTypes = it.getStringArrayList(ARG_IMAGE_TYPES_LIST)
            listOfImageTypesQty = it.getIntegerArrayList(ARG_NUMBER_OF_IMAGE_TYPES_LIST)
            movieId = it.getInt(ARG_MOVIE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryListMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        title?.let {
            binding.toolbarTitle.text = it
        }
        val viewPager = binding.pager
        val tabLayout = binding.tabLayout
        adapter = GalleryListFragmentStateAdapter(
            fragment = this,
            listOfImageTypes = listOfImageTypes!!.toList(),
            listOfImageTypesQty = listOfImageTypesQty!!.toList(),
            movieId = movieId!!
        )
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = defineTabName(position)
        }.attach()
    }

    private fun defineTabName(position: Int): SpannableString {
        val firstPart = "${
            when (listOfImageTypes!![position]) {
                "STILL" -> resources.getString(R.string.still)
                "SHOOTING" -> resources.getString(R.string.shooting)
                "POSTER" -> resources.getString(R.string.poster)
                "FAN_ART" -> resources.getString(R.string.fan_art)
                "PROMO" -> resources.getString(R.string.promo)
                "CONCEPT" -> resources.getString(R.string.concept)
                "WALLPAPER" -> resources.getString(R.string.wallpaper)
                "COVER" -> resources.getString(R.string.cover)
                "SCREENSHOT" -> resources.getString(R.string.screenshot)
                else -> resources.getString(R.string.unknown_key)
            }
        }:"
        val secondPart = " ${listOfImageTypesQty!![position]}"
        val fullName = firstPart + secondPart
        val spannableString = SpannableString(fullName)
        val boldSpan = StyleSpan(Typeface.BOLD)
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.light_gray))
        val indexOfColon = fullName.indexOfLast { it == ':' }
        val indexOfLastChar = fullName.lastIndex
        spannableString.setSpan(
            boldSpan,
            indexOfColon + 1,
            indexOfLastChar + 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            colorSpan,
            indexOfColon + 1,
            indexOfLastChar + 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        return spannableString
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
        private const val ARG_TITLE = "title"
        private const val ARG_IMAGE_TYPES_LIST = "list_of_image_types"
        private const val ARG_NUMBER_OF_IMAGE_TYPES_LIST = "number_of_each_image_type_list"
        private const val ARG_MOVIE_ID = "movie_id"
    }
}