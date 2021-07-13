package com.chibufirst.evote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.databinding.FragmentCandidateSignupBinding
import com.chibufirst.evote.models.Candidate
import com.google.android.material.snackbar.Snackbar

class CandidateSignupFragment : Fragment() {

    private var binding: FragmentCandidateSignupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCandidateSignupBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.signupButton.setOnClickListener { validateUserInputs() }
    }

    private fun validateUserInputs() {
        binding!!.apply {
            when {
                fullnameEditText.text.isEmpty() -> {
                    displayMessage("Full name required.")
                    fullnameEditText.requestFocus()
                    return
                }
                regnoEditText.text.isEmpty() -> {
                    displayMessage("Please enter your registration number.")
                    regnoEditText.requestFocus()
                    return
                }
                bioEditText.text.isEmpty() -> {
                    displayMessage("Say something about yourself.")
                    bioEditText.requestFocus()
                    return
                }
                passwordEditText.text.isEmpty() -> {
                    displayMessage("Password required.")
                    passwordEditText.requestFocus()
                    return
                }
                passwordEditText.text.toString().length < 6 -> {
                    displayMessage("Your password should be at least 6 characters.")
                    passwordEditText.requestFocus()
                    return
                }
                passwordEditText.text.toString() != confirmPasswordEditText.text.toString() -> {
                    displayMessage("The two passwords does not match.")
                    confirmPasswordEditText.requestFocus()
                    return
                }
                else -> {
                    /*val candidate = Candidate(
                        fullnameEditText.text.toString(),
                        "",
                        regnoEditText.text.toString(),
                        genderSpinner.selectedItem.toString(),
                        programSpinner.selectedItem.toString(),
                        levelSpinner.selectedItem.toString(),
                        positionSpinner.selectedItem.toString(),
                        bioEditText.text.toString(),
                        passwordEditText.text.toString()
                    )*/
                    findNavController().navigate(R.id.action_candidateSignupFragment_to_loginFragment)
                }
            }
        }
    }

    private fun displayMessage(msg: String) {
        Snackbar.make(binding!!.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}