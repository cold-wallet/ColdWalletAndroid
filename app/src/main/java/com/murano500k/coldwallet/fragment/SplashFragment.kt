package com.murano500k.coldwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@AndroidEntryPoint
@FragmentScoped
class SplashFragment : Fragment() {

    companion object {
        const val DELAY_MS = 1000L
    }

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            /**
             * Small delay so the user can actually see the splash screen
             * for a moment as feedback of an attempt to retrieve data.
             */
            delay(DELAY_MS)
            try {
                viewModel.initData()
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.action_splashFragment_to_assetListFragment)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                findNavController().navigate(R.id.action_splashFragment_to_errorFragment)
            }
        }
    }
}