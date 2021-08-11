package com.chibufirst.evote.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chibufirst.evote.R
import com.chibufirst.evote.adapters.VoteRecyclerAdapter
import com.chibufirst.evote.databinding.FragmentDashboardVoteBinding
import com.chibufirst.evote.models.Election
import com.chibufirst.evote.models.Nominee
import com.chibufirst.evote.models.Student
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

class DashboardVoteFragment : Fragment() {

    private var binding: FragmentDashboardVoteBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardVoteBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        toggleProgressLayout(true)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear - 1}/$currentYear"
        val drawerLayout: DrawerLayout =
            requireActivity().findViewById(R.id.dashboard_drawer_layout) as DrawerLayout

        binding!!.apply {
            menuTextView.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
            titleText.text = getString(R.string.elections, currentSession)
            val infoText = "Voting updates loading..."
            voteInfoText.text = infoText

            presidentRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            presidentRecycler.setHasFixedSize(true)
            vicePresidentRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            vicePresidentRecycler.setHasFixedSize(true)
            secretaryRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            secretaryRecycler.setHasFixedSize(true)
            assistantSecretaryRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            assistantSecretaryRecycler.setHasFixedSize(true)
            financialSecretaryRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            financialSecretaryRecycler.setHasFixedSize(true)
            ethicsAndResearchRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            ethicsAndResearchRecycler.setHasFixedSize(true)
            socialsRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            socialsRecycler.setHasFixedSize(true)
            publicityRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            publicityRecycler.setHasFixedSize(true)
            welfareRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            welfareRecycler.setHasFixedSize(true)

            db.collection(Constants.EVOTE).document(Constants.ELECTION).get()
                .addOnCompleteListener { electionTask ->
                    if (electionTask.isSuccessful) {
                        val election = electionTask.result?.toObject<Election>()
                        if (election?.status == "STARTED") {
                            db.collection(Constants.USERS).document(auth.currentUser?.email!!).get()
                                .addOnCompleteListener { votingTask ->
                                    if (votingTask.isSuccessful) {
                                        val student = votingTask.result?.toObject<Student>()
                                        if (student?.voted == true) {
                                            notStartedLayout.visibility = View.VISIBLE
                                            startedLayout.visibility = View.GONE
                                            val info =
                                                "Hello ${student.fullName} \nYou've already casted your vote. \nThanks"
                                            voteInfoText.text = info
                                            Util.displayLongMessage(requireContext(), info)
                                            toggleProgressLayout(false)
                                        } else {
                                            notStartedLayout.visibility = View.GONE
                                            startedLayout.visibility = View.VISIBLE
                                            loadCandidates()
                                        }
                                    } else {
                                        Util.displayLongMessage(
                                            requireContext(),
                                            "Error getting voting details: \n${votingTask.exception?.message}"
                                        )
                                    }
                                }
                        } else {
                            notStartedLayout.visibility = View.VISIBLE
                            startedLayout.visibility = View.GONE
                            voteInfoText.text =
                                getString(R.string.the_election_have_not_started_yet)
                            Util.displayLongMessage(
                                requireContext(),
                                getString(R.string.the_election_have_not_started_yet)
                            )
                            toggleProgressLayout(false)
                        }
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error getting election details: \n${electionTask.exception?.message}"
                        )
                    }
                }
        }
    }

    private fun loadCandidates() {
        toggleProgressLayout(true)
        binding!!.apply {
            db.collection(Constants.NOMINEE).document("votes").collection(Constants.PRESIDENT).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val presidentList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val president = nominee.toObject<Nominee>()
                            presidentList.add(president)
                        }
                        val presidentRecyclerAdapter = VoteRecyclerAdapter(
                            presidentList, presidentLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        presidentRecycler.adapter = presidentRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes").collection(Constants.VICE_PRESIDENT)
                .get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val vicePresidentList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val vicePresident = nominee.toObject<Nominee>()
                            vicePresidentList.add(vicePresident)
                        }
                        val vicePresidentRecyclerAdapter = VoteRecyclerAdapter(
                            vicePresidentList, vicePresidentLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        vicePresidentRecycler.adapter = vicePresidentRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes").collection(Constants.SECRETARY).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val secretaryList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val secretary = nominee.toObject<Nominee>()
                            secretaryList.add(secretary)
                        }
                        val secretaryRecyclerAdapter = VoteRecyclerAdapter(
                            secretaryList, secretaryLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        secretaryRecycler.adapter = secretaryRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes")
                .collection(Constants.ASSISTANT_SECRETARY).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val assistantSecretaryList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val assistantSecretary = nominee.toObject<Nominee>()
                            assistantSecretaryList.add(assistantSecretary)
                        }
                        val assistantSecretaryRecyclerAdapter = VoteRecyclerAdapter(
                            assistantSecretaryList, assistantSecretaryLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        assistantSecretaryRecycler.adapter = assistantSecretaryRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes")
                .collection(Constants.FINANCIAL_SECRETARY).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val financialSecretaryList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val financialSecretary = nominee.toObject<Nominee>()
                            financialSecretaryList.add(financialSecretary)
                        }
                        val financialSecretaryRecyclerAdapter = VoteRecyclerAdapter(
                            financialSecretaryList, financialSecretaryLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        financialSecretaryRecycler.adapter = financialSecretaryRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes").collection(Constants.ETHICS).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val ethicsList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val ethics = nominee.toObject<Nominee>()
                            ethicsList.add(ethics)
                        }
                        val ethicsAndResearchRecyclerAdapter = VoteRecyclerAdapter(
                            ethicsList, ethicsAndResearchLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        ethicsAndResearchRecycler.adapter = ethicsAndResearchRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes").collection(Constants.SOCIALS).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val socialsList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val social = nominee.toObject<Nominee>()
                            socialsList.add(social)
                        }
                        val socialsRecyclerAdapter = VoteRecyclerAdapter(
                            socialsList, socialsLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        socialsRecycler.adapter = socialsRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes").collection(Constants.PUBLICITY).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val publicityList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val publicity = nominee.toObject<Nominee>()
                            publicityList.add(publicity)
                        }
                        val publicityRecyclerAdapter = VoteRecyclerAdapter(
                            publicityList, publicityLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        publicityRecycler.adapter = publicityRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
                    }
                }

            db.collection(Constants.NOMINEE).document("votes").collection(Constants.WELFARE).get()
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        val welfareList = arrayListOf<Nominee>()
                        for (nominee in document.result!!) {
                            val welfare = nominee.toObject<Nominee>()
                            welfareList.add(welfare)
                        }
                        val welfareRecyclerAdapter = VoteRecyclerAdapter(
                            welfareList, welfareLayout, baseLayout, auth, db,
                            requireActivity() as DashboardActivity
                        )
                        welfareRecycler.adapter = welfareRecyclerAdapter
                        toggleProgressLayout(false)
                    } else {
                        Util.displayLongMessage(
                            requireContext(),
                            "Error \n${document.exception?.message}"
                        )
                        toggleProgressLayout(false)
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