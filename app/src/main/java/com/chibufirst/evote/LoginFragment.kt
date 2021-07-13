package com.chibufirst.evote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chibufirst.evote.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.loginButton.setOnClickListener { validateUserInputs() }
    }

    private fun validateUserInputs() {
        binding!!.apply {
            when {
                regnoEditText.text.isEmpty() -> {
                    displayMessage("Enter your registration number.")
                    regnoEditText.requestFocus()
                    return
                }
                passwordEditText.text.isEmpty() -> {
                    displayMessage("Enter your password.")
                    passwordEditText.requestFocus()
                    return
                }
                else -> {
                    displayMessage("You are now ready to login.")
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