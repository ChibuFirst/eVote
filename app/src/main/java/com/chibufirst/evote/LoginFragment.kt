package com.chibufirst.evote

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chibufirst.evote.admin.AdminActivity
import com.chibufirst.evote.dashboard.DashboardActivity
import com.chibufirst.evote.databinding.FragmentLoginBinding
import com.chibufirst.evote.util.Constants

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var prefs: SharedPreferences

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

        prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, 0)
        binding!!.loginButton.setOnClickListener { validateUserInputs() }
    }

    private fun validateUserInputs() {
        binding!!.apply {
            val email = emailEditText.text.toString()
            val pword = passwordEditText.text.toString()
            val cemail = prefs.getString(Constants.CEMAIL, "")
            val cpword = prefs.getString(Constants.CPWORD, "")
            val semail = prefs.getString(Constants.SEMAIL, "")
            val spword = prefs.getString(Constants.SPWORD, "")
            when {
                emailEditText.text.isEmpty() -> {
                    displayMessage("Enter your email address.")
                    emailEditText.requestFocus()
                    return
                }
                passwordEditText.text.isEmpty() -> {
                    displayMessage("Enter your password.")
                    passwordEditText.requestFocus()
                    return
                }
                else -> {
                    if (email.equals(Constants.ADMIN, true) && pword.equals(Constants.PASSWORD, false)) {
                        displayMessage("Admin login successful.")
                        requireContext().startActivity(Intent(requireContext(), AdminActivity::class.java))
                        requireActivity().finish()
                    } else if ((cemail.equals(email, true) && cpword.equals(pword, false))) {
                        displayMessage("Candidate login successful.")
                        val intent = Intent(requireContext(), DashboardActivity::class.java)
                        intent.putExtra(Constants.USER, Constants.CANDIDATE)
                        requireContext().startActivity(intent)
                        requireActivity().finish()
                    } else if ((semail.equals(email, true) && spword.equals(pword, false))) {
                        displayMessage("Student login successful.")
                        val intent = Intent(requireContext(), DashboardActivity::class.java)
                        intent.putExtra(Constants.USER, Constants.STUDENT)
                        requireContext().startActivity(intent)
                        requireActivity().finish()
                    } else {
                        displayMessage("Invalid login details.")
                    }
                }
            }
        }
    }

    private fun displayMessage(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}