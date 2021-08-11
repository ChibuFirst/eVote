package com.chibufirst.evote.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chibufirst.evote.MainActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.databinding.FragmentAdminVoteBinding
import com.chibufirst.evote.models.Election
import com.chibufirst.evote.models.Nominee
import com.chibufirst.evote.models.Result
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class AdminVoteFragment : Fragment() {

    private var binding: FragmentAdminVoteBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreListener: ListenerRegistration

    companion object {
        private val TAG: String = AdminVoteFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminVoteBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        var startTime = ""
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
                            startTime = election.startTime.toString()
                            durationText.text = getString(
                                R.string.timer,
                                "Election started: \n${election.startTime}"
                            )
                            startButton.visibility = View.GONE
                            endButton.visibility = View.VISIBLE
                        } else {
                            durationText.text = ""
                            startButton.visibility = View.VISIBLE
                            endButton.visibility = View.GONE
                        }
                    }
                }

            firestoreListener = db.collection(Constants.EVOTE).document(Constants.ELECTION)
                .addSnapshotListener(EventListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "Listen failed!", error)
                        return@EventListener
                    }

                    val election = value?.toObject<Election>()
                    if (election?.status == "STARTED") {
                        startTime = election.startTime.toString()
                        durationText.text =
                            getString(R.string.timer, "Election started: \n${election.startTime}")
                        startButton.visibility = View.GONE
                        endButton.visibility = View.VISIBLE
                    } else {
                        durationText.text = ""
                        startButton.visibility = View.VISIBLE
                        endButton.visibility = View.GONE
                    }
                })

            startButton.setOnClickListener {
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                startTime = currentTime
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                durationText.text = currentTime
                val election = Election("STARTED", currentDate, currentTime)
                db.collection(Constants.EVOTE)
                    .document(Constants.ELECTION)
                    .set(election)
                    .addOnSuccessListener {
                        Util.displayLongMessage(
                            requireContext(),
                            "Election started successfully."
                        )
                    }
                    .addOnFailureListener { e ->
                        Util.displayLongMessage(
                            requireContext(),
                            "Unable to start election: \n${e.message}"
                        )
                    }
                endButton.visibility = View.VISIBLE
            }
            endButton.setOnClickListener {
                val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val currentTime = simpleDateFormat.format(Date())
                val oldDate = simpleDateFormat.parse(startTime)
                val newDate = simpleDateFormat.parse(currentTime)
                val diff = oldDate?.time?.minus(newDate?.time!!)
                val seconds = diff?.div(1000)
                val minutes = abs(seconds?.div(60)!!)
                val hours = abs(minutes.div(60))

                durationText.text = ""
                val electionDuration = "$hours hours, $minutes minutes"
                db.collection(Constants.EVOTE)
                    .document(Constants.ELECTION)
                    .update(
                        mapOf(
                            "status" to "ENDED",
                            "endTime" to currentTime,
                            "duration" to electionDuration
                        )
                    )
                    .addOnSuccessListener {
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.PRESIDENT).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val president = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= president.votes!!) {
                                            nominee = president
                                        }
                                    }
                                    val presidentResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT).document(Constants.PRESIDENT)
                                        .set(presidentResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.VICE_PRESIDENT).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val vicePresident = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= vicePresident.votes!!) {
                                            nominee = vicePresident
                                        }
                                    }
                                    val vicePresidentResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT)
                                        .document(Constants.VICE_PRESIDENT).set(vicePresidentResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.SECRETARY).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val secretary = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= secretary.votes!!) {
                                            nominee = secretary
                                        }
                                    }
                                    val secretaryResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT).document(Constants.SECRETARY)
                                        .set(secretaryResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.ASSISTANT_SECRETARY).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val assistantSecretary = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= assistantSecretary.votes!!) {
                                            nominee = assistantSecretary
                                        }
                                    }
                                    val assistantSecretaryResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT)
                                        .document(Constants.ASSISTANT_SECRETARY)
                                        .set(assistantSecretaryResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.FINANCIAL_SECRETARY).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val financialSecretary = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= financialSecretary.votes!!) {
                                            nominee = financialSecretary
                                        }
                                    }
                                    val financialSecretaryResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT)
                                        .document(Constants.FINANCIAL_SECRETARY)
                                        .set(financialSecretaryResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.ETHICS).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val ethics = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= ethics.votes!!) {
                                            nominee = ethics
                                        }
                                    }
                                    val ethicsResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT).document(Constants.ETHICS)
                                        .set(ethicsResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.SOCIALS).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val socials = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= socials.votes!!) {
                                            nominee = socials
                                        }
                                    }
                                    val socialsResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT).document(Constants.SOCIALS)
                                        .set(socialsResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.PUBLICITY).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val publicity = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= publicity.votes!!) {
                                            nominee = publicity
                                        }
                                    }
                                    val publicityResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT).document(Constants.PUBLICITY)
                                        .set(publicityResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(Constants.WELFARE).get()
                            .addOnCompleteListener { document ->
                                if (document.isSuccessful) {
                                    var nominee = Nominee(votes = 0)
                                    for (nomineeList in document.result!!) {
                                        val welfare = nomineeList.toObject<Nominee>()
                                        if (nominee.votes!! <= welfare.votes!!) {
                                            nominee = welfare
                                        }
                                    }
                                    val welfareResult = Result(
                                        nominee.image,
                                        nominee.name,
                                        nominee.email,
                                        nominee.position,
                                        nominee.votes
                                    )
                                    db.collection(Constants.RESULT).document(Constants.WELFARE)
                                        .set(welfareResult)
                                } else {
                                    Util.displayLongMessage(
                                        requireContext(),
                                        "Error calculating votes: \n${document.exception?.message}"
                                    )
                                }
                            }
                        Util.displayLongMessage(
                            requireContext(),
                            "Election ended successfully."
                        )
                    }
                    .addOnFailureListener { e ->
                        Util.displayLongMessage(
                            requireContext(),
                            "Unable to end election: \n${e.message}"
                        )
                    }
                startButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener.remove()
        binding = null
    }

}