package com.chibufirst.evote.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentDashboardProfileBinding
import com.chibufirst.evote.models.Student
import com.chibufirst.evote.util.Constants
import java.util.*

class DashboardProfileFragment : Fragment() {

    private var binding: FragmentDashboardProfileBinding? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, 0)
        val currentStudent =
            requireActivity().intent.getSerializableExtra(Constants.USER) as Student
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear - 1}/$currentYear"
        val drawerLayout: DrawerLayout =
            requireActivity().findViewById(R.id.dashboard_drawer_layout) as DrawerLayout

        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            nameText.text = currentStudent.fullName
            regnoText.text = currentStudent.regno
            emailText.text = currentStudent.email
            genderText.text = currentStudent.gender
            programText.text = currentStudent.program
            levelText.text = currentStudent.level

            if (currentStudent.user == Constants.CANDIDATE) {
                candidateImage.visibility = View.VISIBLE
                additionalLayout.visibility = View.VISIBLE
                positionText.text = currentStudent.position
                bioText.text = currentStudent.bio
                currentStudent.photo?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .centerCrop()
                        .placeholder(R.drawable.ic_account)
                        .into(candidateImage)
                }
            } else {
                candidateImage.visibility = View.GONE
                additionalLayout.visibility = View.GONE
            }
            menuTextView.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}