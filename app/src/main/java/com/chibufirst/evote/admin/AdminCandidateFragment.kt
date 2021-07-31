package com.chibufirst.evote.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.chibufirst.evote.R
import com.chibufirst.evote.adapters.CandidatesRecyclerAdapter
import com.chibufirst.evote.databinding.FragmentAdminCandidateBinding
import com.chibufirst.evote.models.Nominee
import java.util.*
import kotlin.collections.ArrayList

class AdminCandidateFragment : Fragment() {

    private var binding: FragmentAdminCandidateBinding? = null
    private val args: AdminCandidateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminCandidateBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"

        val demoNomineeList: ArrayList<Nominee> = arrayListOf(
            Nominee(R.drawable.nom1, "John Doe", "HND 2"),
            Nominee(R.drawable.nom2, "Agim Amaka", "ND 2"),
            Nominee(R.drawable.nom3, "Okeke John", "HND 2"),
            Nominee(R.drawable.nom4, "Madu Blessing", "HND 1"),
            Nominee(R.drawable.nom5, "Okaofor James", "ND 2"),
            Nominee(R.drawable.nom6, "Ngozi Melody", "HND 2"),
            Nominee(R.drawable.nom7, "Kingsley Ada", "HND 1"),
            Nominee(R.drawable.nom8, "Joshua Bethel", "HND 1"),
            Nominee(R.drawable.nom9, "Great Uche", "HND 2"),
            Nominee(R.drawable.nom10, "Bright Moses", "HND 2")
        )
        val nomineeAdapter = CandidatesRecyclerAdapter(demoNomineeList)
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            positionTextView.text = args.position
            positionTextView.setOnClickListener { requireActivity().onBackPressed() }
            candidatesRecycler.adapter = nomineeAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}