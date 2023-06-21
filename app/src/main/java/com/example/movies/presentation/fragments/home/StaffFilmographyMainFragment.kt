package com.example.movies.presentation.fragments.home

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.domain.entities.movie.MovieRelatedToStaff
import com.example.domain.entities.staff.StaffFullInfo
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.FragmentStaffFilmographyMainBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.StaffFilmographyFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StaffFilmographyMainFragment : Fragment() {

    private var staffFullInfo: StaffFullInfo? = null
    private var _binding: FragmentStaffFilmographyMainBinding? = null
    private val binding get() = _binding!!
    private var adapter: StaffFilmographyFragmentStateAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT < 33) {
                @Suppress("DEPRECATION")
                staffFullInfo = it.getParcelable(ARG_STAFF_FULL_INFO)
            } else {
                staffFullInfo = it.getParcelable(ARG_STAFF_FULL_INFO, StaffFullInfo::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffFilmographyMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (staffFullInfo == null) return
        binding.toolbarTitle.text = getString(R.string.filmography)
        Log.d(APPLICATION_TAG, staffFullInfo?.personId.toString())
        val generalListOfMovies: List<MovieRelatedToStaff> = staffFullInfo!!.movies
        val listOfProfessions = generalListOfMovies.map { it.professionKey }.distinct()
        val listOfMovieListsByProfession = makeLists(generalListOfMovies, listOfProfessions)
        staffFullInfo?.let { staff ->
            binding.toolbarStaffName.text = defineName(staff)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val viewPager = binding.pager
        val tabLayout = binding.tabLayout
        if (staffFullInfo != null) {
            adapter = StaffFilmographyFragmentStateAdapter(
                this,
                listOfMovieListsByProfession,
                listOfProfessions.size
            )
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = defineTabName(position, listOfMovieListsByProfession)
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun defineName(staffFullInfo: StaffFullInfo): String {
        val currentLocale = resources.configuration.locales[0].country
        return if (currentLocale == "RU" && staffFullInfo.nameRu != null &&
            staffFullInfo.nameRu.toString().isNotBlank()
        ) {
            staffFullInfo.nameRu!!
        } else if (staffFullInfo.nameEn != null &&
            staffFullInfo.nameEn.toString().isNotBlank()
        ) {
            staffFullInfo.nameEn!!
        } else if (staffFullInfo.nameRu != null &&
            staffFullInfo.nameRu.toString().isNotBlank()
        ) {
            staffFullInfo.nameRu!!
        } else {
            ""
        }
    }

    private fun makeLists(
        generalListOfMovies: List<MovieRelatedToStaff>,
        listOfProfessions: List<String>,
    ): List<List<MovieRelatedToStaff>> {
        val listOfMovieListsByProfession = mutableListOf<List<MovieRelatedToStaff>>()
        listOfProfessions.forEach { professionalKey ->
            val list = generalListOfMovies.filter { it.professionKey == professionalKey }
                .distinctBy { it.id }
            listOfMovieListsByProfession.add(list)
        }
        return listOfMovieListsByProfession.sortedBy { it.size }.reversed()
    }

    private fun defineTabName(
        position: Int,
        listOfMovieListsByProfession: List<List<MovieRelatedToStaff>>
    ): SpannableString {
        val tabNameKey = listOfMovieListsByProfession[position].first().professionKey
        val numberOfMoviesInTab = listOfMovieListsByProfession[position].size
        val firstPart = "${
            when (tabNameKey) {
                "WRITER" -> resources.getString(R.string.writer_key)
                "OPERATOR" -> resources.getString(R.string.operator_key)
                "EDITOR" -> resources.getString(R.string.editor_key)
                "COMPOSER" -> resources.getString(R.string.composer_key)
                "PRODUCER_USSR" -> resources.getString(R.string.producer_ussr_key)
                "HIMSELF" -> resources.getString(R.string.himself_key)
                "HERSELF" -> resources.getString(R.string.herself_key)
                "HRONO_TITR_MALE" -> resources.getString(R.string.hrono_titr_male_key)
                "HRONO_TITR_FEMALE" -> resources.getString(R.string.hrono_titr_female_key)
                "TRANSLATOR" -> resources.getString(R.string.translator_key)
                "DIRECTOR" -> resources.getString(R.string.director_key)
                "DESIGN" -> resources.getString(R.string.design_key)
                "PRODUCER" -> resources.getString(R.string.producer_key)
                "ACTOR" -> resources.getString(R.string.actor_key)
                "VOICE_DIRECTOR" -> resources.getString(R.string.voice_director_key)
                "VOICE_MALE" -> resources.getString(R.string.voice_male_key)
                "VOICE_FEMALE" -> resources.getString(R.string.voice_female_key)
                "UNKNOWN" -> resources.getString(R.string.unknown_key)
                else -> resources.getString(R.string.unknown_key)
            }
        }:"
        val secondPart = " $numberOfMoviesInTab"
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

    companion object {
        private const val ARG_STAFF_FULL_INFO = "staff_full_info"
    }
}