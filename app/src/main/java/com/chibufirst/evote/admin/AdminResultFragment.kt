package com.chibufirst.evote.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.chibufirst.evote.MainActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentAdminResultBinding
import com.chibufirst.evote.models.Election
import com.chibufirst.evote.models.Result
import com.chibufirst.evote.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

class AdminResultFragment : Fragment() {

    private var binding: FragmentAdminResultBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminResultBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear - 1}/$currentYear"
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            headerText.setOnClickListener { requireActivity().onBackPressed() }
            logoutImage.setOnClickListener {
                auth.signOut()
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }

            db.collection(Constants.EVOTE).document(Constants.ELECTION).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val election = task.result?.toObject<Election>()
                        if (election?.status == "STARTED") {
                            infoLayout.visibility = View.VISIBLE
                            electionLayout.visibility = View.GONE
                            toggleProgressLayout(false)
                        } else if (election?.status == "ENDED") {
                            infoLayout.visibility = View.GONE
                            electionLayout.visibility = View.VISIBLE
                            toggleProgressLayout(true)

                            db.collection(Constants.RESULT).document(Constants.PRESIDENT).get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedPresidentImage)
                                    electedPresidentNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedPresidentVoteText.text = voteCount
                                    electedPresidentPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.VICE_PRESIDENT).get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedVicePresidentImage)
                                    electedVicePresidentNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedVicePresidentVoteText.text = voteCount
                                    electedVicePresidentPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.SECRETARY).get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedSecretaryImage)
                                    electedSecretaryNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedSecretaryVoteText.text = voteCount
                                    electedSecretaryPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.ASSISTANT_SECRETARY)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedAssistantSecretaryImage)
                                    electedAssistantSecretaryNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedAssistantSecretaryVoteText.text = voteCount
                                    electedAssistantSecretaryPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.FINANCIAL_SECRETARY)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedFinancialSecretaryImage)
                                    electedFinancialSecretaryNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedFinancialSecretaryVoteText.text = voteCount
                                    electedFinancialSecretaryPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.ETHICS).get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedEthicsImage)
                                    electedEthicsNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedEthicsVoteText.text = voteCount
                                    electedEthicsPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.SOCIALS).get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedSocialsImage)
                                    electedSocialsNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedSocialsVoteText.text = voteCount
                                    electedSocialsPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.PUBLICITY).get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedPublicityImage)
                                    electedPublicityNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedPublicityVoteText.text = voteCount
                                    electedPublicityPositionText.text = result.position
                                    toggleProgressLayout(false)
                                }

                            db.collection(Constants.RESULT).document(Constants.WELFARE).get()
                                .addOnSuccessListener { snapshot ->
                                    val result = snapshot.toObject<Result>()
                                    Glide.with(requireContext())
                                        .load(result?.image)
                                        .placeholder(R.drawable.ic_account)
                                        .into(electedWelfareImage)
                                    electedWelfareNameText.text = result?.name
                                    val voteCount =
                                        if (result?.votes!! < 2) "${result.votes} vote" else "${result.votes} votes"
                                    electedWelfareVoteText.text = voteCount
                                    electedWelfarePositionText.text = result.position
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