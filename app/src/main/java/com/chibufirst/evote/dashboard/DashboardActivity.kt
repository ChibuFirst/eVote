package com.chibufirst.evote.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.chibufirst.evote.ExitActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private var binding: ActivityDashboardBinding? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.dashboard_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding!!.dashboardNavView.setupWithNavController(navController)

        val headerView = binding!!.dashboardNavView.getHeaderView(0)
        val imageClose: ImageView = headerView.findViewById(R.id.close_image)
        imageClose.setOnClickListener { binding!!.dashboardDrawerLayout.closeDrawer(GravityCompat.START) }

        binding!!.timerText.text = getString(R.string.timer, "5 hours 32 minutes")
        binding!!.timerText.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (binding!!.dashboardDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding!!.dashboardDrawerLayout.closeDrawer(GravityCompat.START)
        } else if (navController.currentDestination?.id == R.id.dashboardHomeFragment) {
            startActivity(Intent(this, ExitActivity::class.java))
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}