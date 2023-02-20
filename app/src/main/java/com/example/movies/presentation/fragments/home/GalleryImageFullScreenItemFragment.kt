package com.example.movies.presentation.fragments.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.movies.databinding.FragmentGalleryImageFullScreenItemBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.extensions.doIfError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryImageFullScreenItemFragment : Fragment() {

    private var imageUrl: String? = null
    private var currentPage: Int? = null
    private var _binding: FragmentGalleryImageFullScreenItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_IMAGE_URL)
            currentPage = it.getInt(ARG_CURRENT_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryImageFullScreenItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
    }

    private fun loadImage() {
        Glide.with(binding.imageView)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    doIfError(true) {
                        loadImage()
                    }
                    binding.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(binding.imageView)
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
        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_CURRENT_PAGE = "current_page"
    }
}