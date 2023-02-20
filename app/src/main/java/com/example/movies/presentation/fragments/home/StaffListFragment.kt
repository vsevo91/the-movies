package com.example.movies.presentation.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.movies.R
import com.example.movies.databinding.FragmentStaffListBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForVerticalStaffList
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.utilities.SpaceItemDecoration
import com.example.movies.presentation.viewmodels.StaffListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaffListFragment : Fragment() {

    private var title: String? = null
    private var _binding: FragmentStaffListBinding? = null
    private val binding get() = _binding!!
    private val vm: StaffListViewModel by viewModels()
    private var staffList: List<StaffRelatedToMovie>? = null
    private var _adapter: AdapterForVerticalStaffList? = null
    private val adapter get() = _adapter!!
    private var movieId: Int? = null
    private var isActorList: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            movieId = it.getInt(ARG_MOVIE_ID)
            isActorList = it.getBoolean(ARG_IS_ACTOR_LIST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                doIfError(true){
                    vm.clearErrorState()
                    movieId?.let { vm.getStaffByMovieId(it) }
                }
            }
        }
        vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                doIfError(false){
                    vm.clearErrorState()
                    movieId?.let { vm.getStaffByMovieId(it) }
                }
            }
        }
        binding.toolbarTitle.text = title
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        _adapter = AdapterForVerticalStaffList(emptyList())
        adapter.setItemClickListener { staff ->
            val bundle = Bundle().apply {
                putInt(ARG_STAFF_ID, staff.staffId)
            }
            findNavController().navigate(R.id.action_staffListFragment_to_staffFragment, bundle)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(0, 20))
        vm.staffRelatedToMovieLiveData.observe(viewLifecycleOwner) { staffListFull ->
            val staffListFiltered = if(isActorList!!){
                staffListFull.filter { it.professionKey == StaffRelatedToMovie.ACTOR }
            }else{
                staffListFull.filter { it.professionKey != StaffRelatedToMovie.ACTOR }
            }
            this.staffList = staffListFiltered
            this.staffList?.let { adapter.submitList(it) }
        }
        movieId?.let { vm.getStaffByMovieId(it) }
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
        private const val ARG_TITLE = "title"
        private const val ARG_STAFF_ID = "staff_id"
        private const val ARG_IS_ACTOR_LIST = "is_actors_list"
        private const val ARG_MOVIE_ID = "movie_id"
    }
}