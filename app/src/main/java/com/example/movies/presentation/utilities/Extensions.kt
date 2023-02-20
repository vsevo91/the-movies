package com.example.movies.presentation.utilities

import androidx.fragment.app.Fragment
import com.example.movies.presentation.activities.MainActivity

//class Extensions {
fun Fragment.startWithTransparentStatusBar() {
    (requireActivity() as MainActivity).turnOnStatusBarTransparency {
        startPostponedEnterTransition()
        requireParentFragment().parentFragment?.startPostponedEnterTransition()
    }
}

fun Fragment.startWithNonTransparentStatusBar() {
    (requireActivity() as MainActivity).turnOffStatusBarTransparency {
        startPostponedEnterTransition()
        requireParentFragment().parentFragment?.startPostponedEnterTransition()
    }
}
//}