package com.chibufirst.evote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.admin.AdminActivity
import com.chibufirst.evote.dashboard.DashboardActivity
import com.chibufirst.evote.databinding.FragmentLoginBinding
import com.chibufirst.evote.models.Student
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private val TAG: String = LoginFragment::class.java.simpleName
    }

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

        auth = Firebase.auth
        db = Firebase.firestore
        binding!!.apply {
            loginButton.setOnClickListener { validateUserInputs() }
            progressLayout.setOnClickListener(null)
            forgotPasswordText.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment) }
        }
        toggleProgressLayout(false)
    }

    private fun validateUserInputs() {
        Util.hideKeyboard(requireActivity())
        binding!!.apply {
            val email = emailEditText.text.toString()
            val pword = passwordEditText.text.toString()
            when {
                emailEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Enter your email address.")
                    emailEditText.requestFocus()
                    return
                }
                passwordEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Enter your password.")
                    passwordEditText.requestFocus()
                    return
                }
                else -> {
                    toggleProgressLayout(true)
                    auth.signInWithEmailAndPassword(email, pword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                user?.let {
                                    if (it.email != Constants.ADMIN) {
                                        db.collection(Constants.USERS).document(it.email!!)
                                            .get()
                                            .addOnSuccessListener { document ->
                                                if (document != null) {
                                                    Log.d(
                                                        TAG,
                                                        "DocumentSnapshot data: ${document.data}"
                                                    )
                                                    val student = document.toObject<Student>()

                                                    if (student?.user.equals(Constants.CANDIDATE) && student?.approve != true) {
                                                        auth.signOut()
                                                        Util.displayLongMessage(
                                                            requireContext(),
                                                            "Hi ${student?.fullName} \nYou have not been approved by the admin yet!"
                                                        )
                                                        toggleProgressLayout(false)
                                                    } else {
                                                        Util.displayLongMessage(
                                                            requireContext(),
                                                            "Login successful."
                                                        )
                                                        val intent = Intent(
                                                            requireContext(),
                                                            DashboardActivity::class.java
                                                        )
                                                        intent.putExtra(Constants.USER, student)
                                                        requireContext().startActivity(intent)
                                                        requireActivity().finish()
                                                    }
                                                } else {
                                                    Log.d(TAG, "No such document")
                                                    Util.displayLongMessage(
                                                        requireContext(),
                                                        "No such document"
                                                    )
                                                }
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.d(TAG, "get failed with ", exception)
                                                Util.displayLongMessage(
                                                    requireContext(),
                                                    "get failed with \n ${exception.message}"
                                                )
                                                toggleProgressLayout(false)
                                            }
                                    } else {
                                        Util.displayLongMessage(
                                            requireContext(),
                                            "Admin login successful."
                                        )
                                        requireContext().startActivity(
                                            Intent(
                                                requireContext(),
                                                AdminActivity::class.java
                                            )
                                        )
                                        requireActivity().finish()
                                    }
                                }
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Util.displayLongMessage(
                                    requireContext(),
                                    "Authentication failed. \n${task.exception?.message}"
                                )
                                toggleProgressLayout(false)
                            }
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