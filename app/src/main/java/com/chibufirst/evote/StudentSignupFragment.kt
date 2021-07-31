package com.chibufirst.evote

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.databinding.FragmentStudentSignupBinding
import com.chibufirst.evote.util.Constants

class StudentSignupFragment : Fragment() {

    private var binding: FragmentStudentSignupBinding? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStudentSignupBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, 0)
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
                emailEditText.text.isEmpty() -> {
                    displayMessage("Please enter your email address.")
                    emailEditText.requestFocus()
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
                    /*val student = Student(
                        fullnameEditText.text.toString(),
                        regnoEditText.text.toString(),
                        genderSpinner.selectedItem.toString(),
                        programSpinner.selectedItem.toString(),
                        levelSpinner.selectedItem.toString(),
                        passwordEditText.text.toString()
                    )*/
                    prefsEditor = prefs.edit()
                    prefsEditor.putString(Constants.SNAME, fullnameEditText.text.toString())
                    prefsEditor.putString(Constants.SREGNO, regnoEditText.text.toString())
                    prefsEditor.putString(Constants.SEMAIL, emailEditText.text.toString())
                    prefsEditor.putString(Constants.SGENDER, genderSpinner.selectedItem.toString())
                    prefsEditor.putString(Constants.SPROGRAM, programSpinner.selectedItem.toString())
                    prefsEditor.putString(Constants.SLEVEL, levelSpinner.selectedItem.toString())
                    prefsEditor.putString(Constants.SPWORD, passwordEditText.text.toString())
                    prefsEditor.apply()
                    Toast.makeText(requireContext(), "Registration successful.", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_studentSignupFragment_to_loginFragment)
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