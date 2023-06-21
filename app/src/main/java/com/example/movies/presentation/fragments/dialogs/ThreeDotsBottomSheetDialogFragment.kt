package com.example.movies.presentation.fragments.dialogs

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.domain.entities.UserCollection
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.utilities.WATCHED_COLLECTION_ID
import com.example.movies.R
import com.example.movies.databinding.FragmentThreeDotsBottomSheetDialogBinding
import com.example.movies.presentation.adapters.AdapterForUserCollectionsListInDialog
import com.example.movies.presentation.viewmodels.ThreeDotsBottomSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ThreeDotsBottomSheetDialogFragment(
    private val movie: MovieFullInfo
) : BottomSheetDialogFragment() {

    private var _binding: FragmentThreeDotsBottomSheetDialogBinding? = null
    private val binding get() = _binding!!
    private val vm: ThreeDotsBottomSheetViewModel by viewModels()
    private var isFirstListReceiving = true
    private var itemAnimator: RecyclerView.ItemAnimator? = null
    private var _recyclerView: RecyclerView? = null
    private val recyclerView get() = _recyclerView!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThreeDotsBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _recyclerView = binding.recyclerView
        val adapter =
            AdapterForUserCollectionsListInDialog(movie.id)
        recyclerView.adapter = adapter
        itemAnimator = recyclerView.itemAnimator
        allowRecyclerViewToAnimateChanges(false)

        vm.collectionsLiveData.observe(viewLifecycleOwner) {
            val filteredCollections = removeHiddenCollections(it)
            adapter.submitList(filteredCollections)
            adapter.setOnItemClick { userCollection ->
                allowRecyclerViewToAnimateChanges(false)
                vm.addOrDeleteMovieToCollection(movie, userCollection.id!!)
            }
            adapter.setOnDeleteButtonClick { userCollection ->
                allowRecyclerViewToAnimateChanges(true)
                vm.deleteCollection(userCollection)
            }
            adapter.setOnBottomButtonClick {
                allowRecyclerViewToAnimateChanges(true)
                showCollectionCreationDialog()
            }
            scrollToFirstPositionIfFirstReceiving(recyclerView)
        }

        dialog?.setOnShowListener {
            recyclerView.smoothScrollToPosition(0)
        }
        binding.movieName.text = defineMovieName(movie, binding.movieName)
        if (movie.rating != null) {
            binding.rating.text = movie.rating
            binding.rating.isVisible = true
            defineColorOfRatingText(movie.rating!!, binding.rating)
        }
        Glide
            .with(binding.movieImage)
            .load(movie.imageSmall)
            .centerCrop()
            .placeholder(R.drawable.gradient_background)
            .into(binding.movieImage)
        binding.genreText.text = movie.genres?.firstOrNull()

    }

    private fun allowRecyclerViewToAnimateChanges(isAllowed: Boolean) {
        if (isAllowed) {
            recyclerView.itemAnimator = itemAnimator
        } else {
            recyclerView.itemAnimator = null
        }
    }

    private fun checkTextLength(text: String, textView: TextView) {
        val firstWord = text.substringBefore(" ")
        if (firstWord.length > 14) textView.maxLines = 1
        else textView.maxLines = 2
    }

    private fun defineMovieName(movie: MovieGeneral, textView: TextView): String {
        val currentLocale = resources.configuration.locales[0].country
        val text =  if (currentLocale == "RU" && movie.nameRu != null && movie.nameRu.toString()
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
        checkTextLength(text, textView)
        return text
    }

    private fun defineColorOfRatingText(rating: String, textView: TextView) {
        if (rating.contains("%")) return
        val ratingDouble = rating.toDouble()
        when {
            ratingDouble < 5.0 -> {
                textView.background.setTint(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
            }
            5.0 <= ratingDouble && ratingDouble < 7.0 -> {
                textView.background.setTint(
                    ContextCompat.getColor(requireContext(), R.color.medium_gray)
                )
            }
            ratingDouble >= 7.0 -> {
                textView.background.setTint(
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
            }
            else -> throw IllegalStateException()
        }
    }

    private fun scrollToFirstPositionIfFirstReceiving(recyclerView: RecyclerView) {
        if (isFirstListReceiving) {
            recyclerView.scrollToPosition(0)
            isFirstListReceiving = false
        }
    }

    private fun removeHiddenCollections(userCollections: List<UserCollection>): List<UserCollection> {
        return userCollections.filter { !it.isHidden || it.id == WATCHED_COLLECTION_ID }
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
                dialogFragment.dialog!!.cancel()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.enter_acceptable_name),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        dialogFragment.show(parentFragmentManager, DIALOG_TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val DIALOG_TAG = "three_dots_bottom_sheet_dialog"
    }
}