package com.chibufirst.evote.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentDashboardVotingCompletedBinding

class DashboardVotingCompletedFragment : Fragment() {

    private var binding: FragmentDashboardVotingCompletedBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardVotingCompletedBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.backButton.setOnClickListener { findNavController().navigate(R.id.action_dashboardVotingCompletedFragment_to_dashboardHomeFragment) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}