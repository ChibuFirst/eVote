package com.chibufirst.evote.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.chibufirst.evote.ExitActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.ActivityAdminBinding
import com.chibufirst.evote.models.Election
import com.chibufirst.evote.util.Constants
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {

    private var binding: ActivityAdminBinding? = null
    private lateinit var navController: NavController
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreListener: ListenerRegistration

    companion object {
        private val TAG: String = AdminActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        db = Firebase.firestore
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.admin_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

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
        if (navController.currentDestination?.id == R.id.adminHomeFragment) {
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