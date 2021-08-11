package com.chibufirst.evote.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.chibufirst.evote.ExitActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.ActivityDashboardBinding
import com.chibufirst.evote.models.Election
import com.chibufirst.evote.util.Constants
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {

    private var binding: ActivityDashboardBinding? = null
    private lateinit var navController: NavController
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreListener: ListenerRegistration

    companion object {
        private val TAG: String = DashboardActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        db = Firebase.firestore

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding!!.dashboardNavView.setupWithNavController(navController)

        val headerView = binding!!.dashboardNavView.getHeaderView(0)
        val imageClose: ImageView = headerView.findViewById(R.id.close_image)
        imageClose.setOnClickListener { binding!!.dashboardDrawerLayout.closeDrawer(GravityCompat.START) }

        db.collection(Constants.EVOTE).document(Constants.ELECTION).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val election = task.result?.toObject<Election>()
                    if (election?.status == "STARTED") {
                        binding!!.timerText.text =
                            getString(R.string.timer, "Election started: ${election.startTime}")
                        binding!!.timerText.visibility = View.VISIBLE
                    } else {
                        binding!!.timerText.visibility = View.GONE
                    }
                }
            }

        firestoreListener = db.collection(Constants.EVOTE).document(Constants.ELECTION)
            .addSnapshotListener(EventListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed!", error)
                    return@EventListener
                }

                val election = value?.toObject<Election>()
                if (election?.status == "STARTED") {
                    binding!!.timerText.text =
                        getString(R.string.timer, "Election started: ${election.startTime}")
                    binding!!.timerText.visibility = View.VISIBLE
                } else {
                    binding!!.timerText.visibility = View.GONE
                }
            })
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
        firestoreListener.remove()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}