package com.chibufirst.evote

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.databinding.FragmentCandidateSignupBinding
import com.chibufirst.evote.util.Constants
import java.io.FileNotFoundException

class CandidateSignupFragment : Fragment() {

    private var binding: FragmentCandidateSignupBinding? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                try {
                    val imageStream = requireContext().contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    binding!!.photoImage.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Something went wrong.", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Nothing Selected.", Toast.LENGTH_LONG).show()
            }
        }

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

        prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, 0)

        binding!!.apply {
            chooseButton.setOnClickListener { selectPhoto() }
            signupButton.setOnClickListener { validateUserInputs() }
        }
    }

    private fun selectPhoto() {
        val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
        imageIntent.type = "image/*"
        startForResult.launch(imageIntent)
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
                    prefsEditor = prefs.edit()
                    prefsEditor.putString(Constants.CNAME, fullnameEditText.text.toString())
                    prefsEditor.putString(Constants.CREGNO, regnoEditText.text.toString())
                    prefsEditor.putString(Constants.CEMAIL, emailEditText.text.toString())
                    prefsEditor.putString(Constants.CGENDER, genderSpinner.selectedItem.toString())
                    prefsEditor.putString(Constants.CPROGRAM, programSpinner.selectedItem.toString())
                    prefsEditor.putString(Constants.CLEVEL, levelSpinner.selectedItem.toString())
                    prefsEditor.putString(Constants.CPOSITION, positionSpinner.selectedItem.toString())
                    prefsEditor.putString(Constants.CBIO, bioEditText.text.toString())
                    prefsEditor.putString(Constants.CPWORD, passwordEditText.text.toString())
                    prefsEditor.apply()
                    Toast.makeText(requireContext(), "Registration successful.", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_candidateSignupFragment_to_loginFragment)
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