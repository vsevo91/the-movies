package com.example.movies.presentation.fragments.main

import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.movies.R
import com.example.movies.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.bottom_navigation_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            onNavDestinationSelected(menuItem, navController)
            true
        }
        bottomNavigationView.setOnItemReselectedListener {
            val rootFragment = navHostFragment.childFragmentManager.fragments.first()
            when (it.itemId) {
                R.id.homeRootFragment -> {
                    val currentFragment = rootFragment.childFragmentManager.findFragmentById(R.id.home_root_fragment_container)
                    currentFragment?.findNavController()?.popBackStack(R.id.homeFragment, false)
                }
                R.id.searchRootFragment -> {
                    val currentFragment = rootFragment.childFragmentManager.findFragmentById(R.id.search_root_fragment_container)
                    currentFragment?.findNavController()?.popBackStack(R.id.searchFragment, false)
                }
                R.id.profileRootFragment -> {
                    val currentFragment = rootFragment.childFragmentManager.findFragmentById(R.id.profile_root_fragment_container)
                    currentFragment?.findNavController()?.popBackStack(R.id.profileFragment, false)
                }
            }
        }
    }

    private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().id,
                inclusive = false,
                saveState = true
            )
        }
        val options = builder.build()
        return try {
            navController.navigate(item.itemId, null, options)
            navController.currentDestination?.matchDestination(item.itemId) == true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}