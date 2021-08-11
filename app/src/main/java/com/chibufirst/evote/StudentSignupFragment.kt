package com.chibufirst.evote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.databinding.FragmentStudentSignupBinding
import com.chibufirst.evote.models.Student
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StudentSignupFragment : Fragment() {

    private var binding: FragmentStudentSignupBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private val TAG: String = StudentSignupFragment::class.java.simpleName
    }

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

        auth = Firebase.auth
        db = Firebase.firestore
        binding!!.signupButton.setOnClickListener { validateUserInputs() }
        binding!!.progressLayout.setOnClickListener(null)
        toggleProgressLayout(false)
    }

    private fun validateUserInputs() {
        Util.hideKeyboard(requireActivity())
        binding!!.apply {
            when {
                fullnameEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Full name required.")
                    fullnameEditText.requestFocus()
                    return
                }
                regnoEditText.text.isEmpty() -> {
                    Util.displayLongMessage(
                        requireContext(),
                        "Please enter your registration number."
                    )
                    regnoEditText.requestFocus()
                    return
                }
                emailEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Please enter your email address.")
                    emailEditText.requestFocus()
                    return
                }
                passwordEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Password required.")
                    passwordEditText.requestFocus()
                    return
                }
                passwordEditText.text.toString().length < 6 -> {
                    Util.displayLongMessage(
                        requireContext(),
                        "Your password should be at least 6 characters."
                    )
                    passwordEditText.requestFocus()
                    return
                }
                passwordEditText.text.toString() != confirmPasswordEditText.text.toString() -> {
                    Util.displayLongMessage(requireContext(), "The two passwords does not match.")
                    confirmPasswordEditText.requestFocus()
                    return
                }
                else -> {
                    val student = Student(
                        Constants.STUDENT,
                        fullnameEditText.text.toString(),
                        regnoEditText.text.toString(),
                        emailEditText.text.toString(),
                        genderSpinner.selectedItem.toString(),
                        programSpinner.selectedItem.toString(),
                        levelSpinner.selectedItem.toString()
                    )
                    createAccount(student, passwordEditText.text.toString())
                }
            }
        }
    }

    private fun createAccount(student: Student, password: String) {
        toggleProgressLayout(true)
        auth.createUserWithEmailAndPassword(student.email!!, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = student.fullName
                    }
                    user!!.updateProfile(profileUpdates)
                    db.collection(Constants.USERS)
                        .document(student.email!!)
                        .set(student)
                        .addOnSuccessListener {
                            Util.displayLongMessage(
                                requireContext(),
                                "Details saved"
                            )
                        }
                        .addOnFailureListener { e ->
                            Util.displayLongMessage(
                                requireContext(),
                                "Error saving details: \n${e.message}"
                            )
                        }
                    Util.displayLongMessage(requireContext(), "Registration successful.")
                    findNavController().navigate(R.id.action_studentSignupFragment_to_loginFragment)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Util.displayLongMessage(
                        requireContext(),
                        "Registration was not successful.\n${task.exception?.message}"
                    )
                    toggleProgressLayout(false)
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