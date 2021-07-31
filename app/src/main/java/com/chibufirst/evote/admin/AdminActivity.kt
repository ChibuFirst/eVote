package com.chibufirst.evote.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.chibufirst.evote.ExitActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private var binding: ActivityAdminBinding? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.admin_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding!!.timerText.text = getString(R.string.timer, "5 hours 32 minutes")
        binding!!.timerText.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.adminHomeFragment) {
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