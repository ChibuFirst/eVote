package com.chibufirst.evote.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentDashboardHomeBinding
import java.util.*

class DashboardHomeFragment : Fragment() {

    private var binding: FragmentDashboardHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(),
            R.layout.position_list_item, resources.getStringArray(R.array.position))
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"
        val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.dashboard_drawer_layout) as DrawerLayout
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            menuImage.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
            notificationImage.setOnClickListener { findNavController().navigate(R.id.action_dashboardHomeFragment_to_dashboardNotificationFragment) }
            positionList.adapter = adapter
            positionList.setOnItemClickListener { _, _, i, _ ->
                val item: String = positionList.getItemAtPosition(i).toString()
                val action = DashboardHomeFragmentDirections.actionDashboardHomeFragmentToDashboardPositionFragment(item)
                positionList.findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}