package com.chibufirst.evote.dashboard

import android.os.Bundle
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
import java.util.*

class DashboardNotificationFragment : Fragment() {

    private var binding: FragmentDashboardNotificationBinding? = null

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

        val demoNotifications = arrayListOf(
            Notifications(
                "Welcome to this portal for the ELECTION, feel free to explore and view different positions and their candidates.",
                "12/07/2021"
            ),
            Notifications(
                "The date for this year's election will be communicated soon, get to decide those that will lead.",
                "16/07/2021"
            ),
            Notifications(
                "Please don't forget this: If you don't vote, you loose the right to complain.",
                "18/07/2021"
            ),
            Notifications("Just know that your vote counts.", "20/07/2021"),
            Notifications(
                "Welcome to this portal for the ELECTION, feel free to explore and view different positions and their candidates.",
                "12/07/2021"
            ),
            Notifications(
                "The date for this year's election will be communicated soon, get to decide those that will lead.",
                "16/07/2021"
            ),
            Notifications(
                "Please don't forget this: If you don't vote, you loose the right to complain.",
                "18/07/2021"
            ),
            Notifications("Just know that your vote counts.", "20/07/2021")
        )
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"
        val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.dashboard_drawer_layout) as DrawerLayout
        binding!!.apply {
            menuTextView.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
            titleText.text = getString(R.string.elections, currentSession)
            notificationsRecycler.setHasFixedSize(true)
            if (demoNotifications.isEmpty()) {
                notificationInfoLayout.visibility = View.VISIBLE
                notificationsRecycler.visibility = View.GONE
            } else {
                notificationInfoLayout.visibility = View.GONE
                notificationsRecycler.visibility = View.VISIBLE
                val notificationsAdapter = NotificationsRecyclerAdapter(demoNotifications)
                notificationsRecycler.adapter = notificationsAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}