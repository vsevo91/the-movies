package com.example.movies.presentation.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager.LayoutParams
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.viewbinding.ViewBinding
import com.example.movies.databinding.ActivityMainWithOnboardingBinding
import com.example.movies.databinding.ActivityMainWithoutOnboardingBinding
import com.example.movies.presentation.viewmodels.MainActivityViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ViewBinding? = null
    private val binding get() = _binding!!
    private val vm: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.ifShow.observe(this) { ifShow ->
            _binding = if (!ifShow) {
                ActivityMainWithoutOnboardingBinding.inflate(layoutInflater)
            } else {
                ActivityMainWithOnboardingBinding.inflate(layoutInflater)
            }
            addOnPreDrawListenerForSplashScreenDelaying()
            setContentView(binding.root)
        }
        vm.ifShowOnboardingScreen()
    }

    private fun addOnPreDrawListenerForSplashScreenDelaying() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val content: View = binding.root
            content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return if (vm.isReady) {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        } else {
                            false
                        }
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun showSnack(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun turnOnStatusBarTransparency(exceptView: View? = null, callback: (() -> Unit)? = null) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }
            exceptView?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }
        callback?.invoke()
    }

    fun turnOffStatusBarTransparency(callback: (() -> Unit)? = null) {
        if (_binding == null) return
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val attrs = intArrayOf(com.google.android.material.R.attr.colorPrimaryVariant)
        val thisAttributes = this.obtainStyledAttributes(attrs)
        window.statusBarColor = thisAttributes.getColor(0, 0)
        thisAttributes.recycle()
        binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = 0
            bottomMargin = 0
            rightMargin = 0
        }
        callback?.invoke()
    }
}