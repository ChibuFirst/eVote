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
import com.chibufirst.evote.util.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class AdminHomeFragment : Fragment() {

    private var binding: FragmentAdminHomeBinding? = null
    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear - 1}/$currentYear"
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            logoutImage.setOnClickListener {
                auth.signOut()
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            candidateCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminPositionFragment) }
            notificationCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminNotificationFragment2) }
            electionCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminVoteFragment) }
            resultCard.setOnClickListener { findNavController().navigate(R.id.action_adminHomeFragment_to_adminResultFragment) }
            clearImage.setOnLongClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle("Clear database?")
                    .setMessage("You're about to clear the entire database. \n(This is permanent and can't be undone)")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Clear") { dialog, _ ->
                        Util.displayLongMessage(
                            requireContext(),
                            "For your mind, I go allow you do am? \n:("
                        )
                        dialog.dismiss()
                    }
                    .create().show()
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}