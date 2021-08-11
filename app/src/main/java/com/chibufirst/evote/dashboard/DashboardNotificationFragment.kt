package com.chibufirst.evote.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.chibufirst.evote.R
import com.chibufirst.evote.adapters.NotificationsRecyclerAdapter
import com.chibufirst.evote.databinding.FragmentDashboardNotificationBinding
import com.chibufirst.evote.models.Notifications
import com.chibufirst.evote.util.Constants
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

class DashboardNotificationFragment : Fragment() {

    private var binding: FragmentDashboardNotificationBinding? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreListener: ListenerRegistration
    private lateinit var notificationsAdapter: NotificationsRecyclerAdapter

    companion object {
        private val TAG: String = DashboardNotificationFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardNotificationBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.firestore

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear - 1}/$currentYear"
        val drawerLayout: DrawerLayout =
            requireActivity().findViewById(R.id.dashboard_drawer_layout) as DrawerLayout

        binding!!.apply {
            menuTextView.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
            titleText.text = getString(R.string.elections, currentSession)
            notificationsRecycler.setHasFixedSize(true)
        }

        db.collection(Constants.NOTIFICATIONS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get(Source.CACHE)
            .addOnCompleteListener { task ->
                val notificationsList = arrayListOf<Notifications>()
                if (task.isSuccessful) {
                    for (doc in task.result!!) {
                        val notifications = doc.toObject<Notifications>()
                        notificationsList.add(notifications)
                    }
                }
                binding!!.apply {
                    if (notificationsList.isEmpty()) {
                        notificationInfoLayout.visibility = View.VISIBLE
                        notificationsRecycler.visibility = View.GONE
                    } else {
                        notificationInfoLayout.visibility = View.GONE
                        notificationsRecycler.visibility = View.VISIBLE
                        notificationsAdapter = NotificationsRecyclerAdapter(notificationsList)
                        notificationsRecycler.adapter = notificationsAdapter
                    }
                }
            }

        firestoreListener = db.collection(Constants.NOTIFICATIONS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed!", error)
                    return@EventListener
                }

                val notifyList = arrayListOf<Notifications>()
                for (doc in value!!) {
                    val notifications = doc.toObject<Notifications>()
                    notifyList.add(notifications)
                }
                binding!!.apply {
                    if (notifyList.isEmpty()) {
                        notificationInfoLayout.visibility = View.VISIBLE
                        notificationsRecycler.visibility = View.GONE
                    } else {
                        notificationInfoLayout.visibility = View.GONE
                        notificationsRecycler.visibility = View.VISIBLE
                        notificationsAdapter = NotificationsRecyclerAdapter(notifyList)
                        notificationsRecycler.adapter = notificationsAdapter
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener.remove()
        binding = null
    }

}