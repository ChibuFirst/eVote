package com.chibufirst.evote.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import com.chibufirst.evote.MainActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentAdminPositionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class AdminPositionFragment : Fragment() {

    private var binding: FragmentAdminPositionBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminPositionBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val adapter = ArrayAdapter(requireContext(),
            R.layout.position_list_item, resources.getStringArray(R.array.position))
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            headerText.setOnClickListener { requireActivity().onBackPressed() }
            logoutImage.setOnClickListener {
                auth.signOut()
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            positionList.adapter = adapter
            positionList.setOnItemClickListener { _, _, i, _ ->
                val item: String = positionList.getItemAtPosition(i).toString()
                val action = AdminPositionFragmentDirections.actionAdminPositionFragmentToAdminCandidateFragment(item)
                positionList.findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}