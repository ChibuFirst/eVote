package com.chibufirst.evote.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.MainActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentAdminHomeBinding
import java.util.*

class AdminHomeFragment : Fragment() {

    private var binding: FragmentAdminHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            logoutImage.setOnClickListener {
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            candidateCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminPositionFragment) }
            notificationCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminNotificationFragment2) }
            electionCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminVoteFragment) }
            resultCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminResultFragment) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}