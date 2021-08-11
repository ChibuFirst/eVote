package com.chibufirst.evote.adapters

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.chibufirst.evote.R
import com.chibufirst.evote.dashboard.DashboardActivity
import com.chibufirst.evote.models.Nominee
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class VoteRecyclerAdapter(
    private val nomineeList: ArrayList<Nominee>,
    private val positionLayout: LinearLayout,
    private val baseLayout: LinearLayout,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val activity: DashboardActivity
) : RecyclerView.Adapter<VoteRecyclerAdapter.VoteRecyclerViewHolder>() {

    private var prefs: SharedPreferences =
        baseLayout.context.getSharedPreferences(Constants.PREF_NAME, 0)

    init {
        prefs.edit().putInt(Constants.LAYOUT_COUNT, 0).apply()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteRecyclerViewHolder {
        return VoteRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.vote_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VoteRecyclerViewHolder, position: Int) {
        val nominee = nomineeList[position]
        holder.bind(nominee)
        holder.voteButton.setOnClickListener {
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setCancelable(false)
                .setTitle("Vote this candidate?")
                .setMessage("You're about to vote for ${nominee.name} as ${nominee.position}. \n(This is permanent and can't be undone)")
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("Vote") { dialogInterface, _ ->
                    holder.voteCard.isChecked = !holder.voteCard.isChecked
                    db.collection(Constants.NOMINEE).document("votes")
                        .collection(nominee.position!!).document(nominee.email!!)
                        .update("votes", FieldValue.increment(1))
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                prefs.edit()
                                    .putInt(
                                        Constants.LAYOUT_COUNT,
                                        prefs.getInt(Constants.LAYOUT_COUNT, 0) + 1
                                    )
                                    .apply()
                                db.collection(Constants.USERS).document(auth.currentUser?.email!!)
                                    .update("voted", true)
                                    .addOnFailureListener { e ->
                                        Util.displayLongMessage(
                                            holder.itemView.context,
                                            "Error encountered: \n${e.message}"
                                        )
                                    }
                            } else {
                                Util.displayLongMessage(
                                    holder.itemView.context,
                                    "Error: \n${updateTask.exception?.message}."
                                )
                            }
                        }

                    Handler(Looper.getMainLooper()).postDelayed({
                        TransitionManager.beginDelayedTransition(baseLayout, AutoTransition())
                        positionLayout.visibility = View.GONE

                        if (prefs.getInt(Constants.LAYOUT_COUNT, 0) > 8) {
                            val navController = Navigation.findNavController(
                                activity,
                                R.id.dashboard_nav_host_fragment
                            )
                            navController.navigateUp()
                            navController.navigate(R.id.dashboardVotingCompletedFragment)
                        }
                    }, 1000)
                    dialogInterface.dismiss()
                }
                .create().show()
        }
    }

    override fun getItemCount(): Int = nomineeList.size

    class VoteRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val voteCard: MaterialCardView = itemView.findViewById(R.id.vote_card)
        private val candidateImage: ImageView = itemView.findViewById(R.id.candidate_profile_image)
        private val candidateFullNameText: TextView =
            itemView.findViewById(R.id.candidate_fullname_text)
        private val candidateVoteText: TextView = itemView.findViewById(R.id.candidate_vote_text)
        val voteButton: Button = itemView.findViewById(R.id.vote_button)

        fun bind(nominee: Nominee) {
            Glide.with(itemView.context)
                .load(nominee.image)
                .placeholder(R.drawable.ic_account)
                .into(candidateImage)
            candidateFullNameText.text = nominee.name
            val voteCount =
                if (nominee.votes!! < 2) "${nominee.votes} vote" else "${nominee.votes} votes"
            candidateVoteText.text = voteCount
        }
    }
}