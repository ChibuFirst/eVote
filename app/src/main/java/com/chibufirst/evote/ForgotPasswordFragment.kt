package com.chibufirst.evote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chibufirst.evote.databinding.FragmentForgotPasswordBinding
import com.chibufirst.evote.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordFragment : Fragment() {

    private var binding: FragmentForgotPasswordBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        binding!!.apply {
            progressLayout.setOnClickListener(null)
            submitButton.setOnClickListener { validateUserInputs() }
        }
        toggleProgressLayout(false)
    }

    private fun validateUserInputs() {
        Util.hideKeyboard(requireActivity())
        binding!!.apply {
            val email = emailEditText.text.toString()
            when {
                emailEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Enter your email address.")
                    emailEditText.requestFocus()
                    return
                }
                else -> {
                    toggleProgressLayout(true)
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Util.displayLongMessage(
                                    requireContext(),
                                    "Email sent... \nCheck your email and reset your password."
                                )
                                requireActivity().onBackPressed()
                            } else {
                                Util.displayLongMessage(
                                    requireContext(),
                                    "Error encountered: \n${task.exception?.message}"
                                )
                            }
                            toggleProgressLayout(false)
                        }
                }
            }
        }
    }

    private fun toggleProgressLayout(isShown: Boolean) {
        if (isShown) {
            binding!!.progressLayout.visibility = View.VISIBLE
        } else {
            binding!!.progressLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}