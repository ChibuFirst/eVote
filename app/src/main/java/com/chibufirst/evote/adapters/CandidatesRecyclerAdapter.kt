package com.chibufirst.evote.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chibufirst.evote.R
import com.chibufirst.evote.models.Election
import com.chibufirst.evote.models.Nominee
import com.chibufirst.evote.models.Student
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class CandidatesRecyclerAdapter(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val candidatesList: ArrayList<Student>
) :
    RecyclerView.Adapter<CandidatesRecyclerAdapter.CandidatesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatesViewHolder {
        return CandidatesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.candidates_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CandidatesViewHolder, position: Int) {
        val candidate = candidatesList[position]
        db.collection(Constants.EVOTE).document(Constants.ELECTION).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val election = task.result?.toObject<Election>()
                    if (election?.status == "STARTED") {
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(candidate.position!!)
                            .document(candidate.email!!).get()
                            .addOnSuccessListener { document ->
                                val nominee = document.toObject<Nominee>()
                                holder.voteText.visibility = View.VISIBLE
                                holder.bindVote(nominee!!)
                            }
                            .addOnFailureListener { holder.voteText.visibility = View.GONE }
                        db.collection(Constants.NOMINEE).document("votes")
                            .collection(candidate.position!!)
                            .document(candidate.email!!)
                            .addSnapshotListener(EventListener { value, error ->
                                if (error != null) {
                                    Log.e("TAG", "Listen failed!", error)
                                    return@EventListener
                                }
                                val nominee = value?.toObject<Nominee>()
                                holder.voteText.visibility = View.VISIBLE
                                holder.bindVote(nominee!!)
                            })
                    } else {
                        holder.voteText.visibility = View.VISIBLE
                    }
                } else {
                    holder.voteText.visibility = View.VISIBLE
                }
            }
        holder.bind(candidate)
        holder.itemView.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(it.context)
            val bottomSheetView: View? = if (auth.currentUser?.email == Constants.ADMIN) {
                LayoutInflater.from(it.context)
                    .inflate(
                        R.layout.layout_candidate_profile_approval,
                        holder.itemView.findViewById(android.R.id.content)
                    )
            } else {
                LayoutInflater.from(it.context)
                    .inflate(
                        R.layout.layout_candidate_profile,
                        holder.itemView.findViewById(android.R.id.content)
                    )
            }
            val candidateImage: ImageView = bottomSheetView!!.findViewById(R.id.candidate_image)
            val candidateNameText: TextView = bottomSheetView.findViewById(R.id.candidate_name_text)
            val candidateLevelText: TextView =
                bottomSheetView.findViewById(R.id.candidate_level_text)
            val candidateBioText: TextView = bottomSheetView.findViewById(R.id.candidate_bio_text)

            if (auth.currentUser?.email == Constants.ADMIN) {
                val approveButton: Button = bottomSheetView.findViewById(R.id.approve_button)
                if (candidate.approve == true) {
                    approveButton.visibility = View.GONE
                } else {
                    approveButton.setOnClickListener {
                        approveButton.isEnabled = false
                        db.collection(Constants.USERS).document(candidate.email!!)
                            .update("approve", true)
                            .addOnSuccessListener {
                                val nominee = Nominee(
                                    candidate.photo,
                                    candidate.fullName,
                                    candidate.email,
                                    candidate.position,
                                    candidate.level,
                                    0
                                )
                                db.collection(Constants.NOMINEE)
                                    .document("votes")
                                    .collection(nominee.position!!)
                                    .document(nominee.email!!)
                                    .set(nominee)
                                    .addOnSuccessListener {
                                        Util.displayLongMessage(
                                            approveButton.context,
                                            "Candidate approved successfully."
                                        )
                                        bottomSheetDialog.dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Util.displayLongMessage(
                                            approveButton.context,
                                            "Error: \n${e.message}"
                                        )
                                        bottomSheetDialog.dismiss()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Util.displayLongMessage(it.context, "Error: \n${e.message}")
                                bottomSheetDialog.dismiss()
                            }
                        approveButton.isEnabled = true
                    }
                }
            }

            Glide.with(it.context)
                .load(candidate.photo)
                .placeholder(R.drawable.ic_account)
                .into(candidateImage)
            candidateNameText.text = candidate.fullName
            candidateLevelText.text = candidate.level
            candidateBioText.text = candidate.bio
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

    override fun getItemCount(): Int = candidatesList.size

    class CandidatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoImage: ImageView = itemView.findViewById(R.id.photo_image)
        private val nameText: TextView = itemView.findViewById(R.id.name_text)
        private val levelText: TextView = itemView.findViewById(R.id.level_text)
        val voteText: TextView = itemView.findViewById(R.id.vote_text)

        fun bind(candidate: Student) {
            Glide.with(itemView.context)
                .load(candidate.photo)
                .placeholder(R.drawable.ic_account)
                .into(photoImage)
            nameText.text = candidate.fullName
            levelText.text = candidate.level
        }

        fun bindVote(nominee: Nominee) {
            val voteCount =
                if (nominee.votes!! < 2) "${nominee.votes} vote" else "${nominee.votes} votes"
            voteText.text = voteCount
        }
    }
}