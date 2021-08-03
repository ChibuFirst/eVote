package com.chibufirst.evote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.admin.AdminActivity
import com.chibufirst.evote.dashboard.DashboardActivity
import com.chibufirst.evote.databinding.FragmentLandingPageBinding
import com.chibufirst.evote.models.Student
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class LandingPageFragment : Fragment() {

    private var binding: FragmentLandingPageBinding? = null
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
        binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        binding!!.apply {
            studentButton.setOnClickListener { findNavController().navigate(R.id.action_landingPageFragment_to_studentSignupFragment) }
            candidateButton.setOnClickListener { findNavController().navigate(R.id.action_landingPageFragment_to_candidateSignupFragment) }
            loginText.setOnClickListener { findNavController().navigate(R.id.action_landingPageFragment_to_loginFragment) }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            if (it.email != Constants.ADMIN) {
                db.collection(Constants.USERS).document(it.email!!)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                            val student = document.toObject<Student>()
                            Util.displayLongMessage(requireContext(), "Login Successful.")

                            val intent = Intent(requireContext(), DashboardActivity::class.java)
                            intent.putExtra(Constants.USER, student)
                            requireContext().startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Log.d(TAG, "No such document")
                            Util.displayLongMessage(requireContext(), "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                        Util.displayLongMessage(requireContext(), "get failed with \n ${exception.message}")
                    }
            } else {
                Util.displayLongMessage(requireContext(), "Admin login successful.")
                requireContext().startActivity(Intent(requireContext(), AdminActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}