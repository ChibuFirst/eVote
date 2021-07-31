package com.chibufirst.evote.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentDashboardVoteBinding
import java.util.*

class DashboardVoteFragment : Fragment() {

    private var binding: FragmentDashboardVoteBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardVoteBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"
        val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.dashboard_drawer_layout) as DrawerLayout
        binding!!.apply {
            menuTextView.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
            titleText.text = getString(R.string.elections, currentSession)
            continueButton.setOnClickListener {
                voteIntroLayout.visibility = View.GONE
                voteCodeLayout.visibility = View.VISIBLE
            }
            code1EditText.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}