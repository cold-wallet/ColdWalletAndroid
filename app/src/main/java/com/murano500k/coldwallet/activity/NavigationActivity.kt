package com.murano500k.coldwallet.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.net.utils.Status
import com.murano500k.coldwallet.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.activity_navigation.*

@AndroidEntryPoint
@ActivityScoped
class NavigationActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        setupObserver(navController)
        splashViewModel.fetchData()
    }

    fun setupObserver(navController: NavController) {
        splashViewModel.getStatus().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    navController.navigate(R.id.action_splashFragment_to_assetListFragment)
                }
                Status.LOADING -> {
                    bottomNavigationView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    navController.navigate(R.id.action_splashFragment_to_errorFragment)
                }
            }
        })
    }
}