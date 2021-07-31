package com.chibufirst.evote.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentDashboardProfileBinding
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
        val currentUser = requireActivity().intent.getStringExtra(Constants.USER)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"
        val drawerLayout: DrawerLayout =
            requireActivity().findViewById(R.id.dashboard_drawer_layout) as DrawerLayout
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            if (currentUser == Constants.CANDIDATE) {
                candidateImage.visibility = View.VISIBLE
                additionalLayout.visibility = View.VISIBLE

                nameText.text = prefs.getString(Constants.CNAME, "")
                regnoText.text = prefs.getString(Constants.CREGNO, "")
                emailText.text = prefs.getString(Constants.CEMAIL, "")
                genderText.text = prefs.getString(Constants.CGENDER, "")
                programText.text = prefs.getString(Constants.CPROGRAM, "")
                levelText.text = prefs.getString(Constants.CLEVEL, "")
                positionText.text = prefs.getString(Constants.CPOSITION, "")
                bioText.text = prefs.getString(Constants.CBIO, "")
            } else {
                candidateImage.visibility = View.GONE
                additionalLayout.visibility = View.GONE

                nameText.text = prefs.getString(Constants.SNAME, "")
                regnoText.text = prefs.getString(Constants.SREGNO, "")
                emailText.text = prefs.getString(Constants.SEMAIL, "")
                genderText.text = prefs.getString(Constants.SGENDER, "")
                programText.text = prefs.getString(Constants.SPROGRAM, "")
                levelText.text = prefs.getString(Constants.SLEVEL, "")
            }
            menuTextView.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}