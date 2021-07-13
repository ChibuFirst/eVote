package com.chibufirst.evote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.databinding.FragmentLandingPageBinding

class LandingPageFragment : Fragment() {

    private var binding: FragmentLandingPageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.apply {
            studentButton.setOnClickListener { findNavController().navigate(R.id.action_landingPageFragment_to_studentSignupFragment) }
            candidateButton.setOnClickListener { findNavController().navigate(R.id.action_landingPageFragment_to_candidateSignupFragment) }
            loginText.setOnClickListener { findNavController().navigate(R.id.action_landingPageFragment_to_loginFragment) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}