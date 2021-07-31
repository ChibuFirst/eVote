package com.chibufirst.evote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chibufirst.evote.R
import com.chibufirst.evote.models.Nominee
import com.google.android.material.bottomsheet.BottomSheetDialog

class CandidatesRecyclerAdapter(private val nomineeList: ArrayList<Nominee>) :
    RecyclerView.Adapter<CandidatesRecyclerAdapter.CandidatesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatesViewHolder {
        return CandidatesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.candidates_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CandidatesViewHolder, position: Int) {
        val nominee = nomineeList[position]
        holder.bind(nominee)


        holder.itemView.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(it.context)
            val bottomSheetView = LayoutInflater.from(it.context)
                .inflate(
                    R.layout.fragment_profile,
                    holder.itemView.findViewById(android.R.id.content)
                )
            val nomineeImage: ImageView = bottomSheetView.findViewById(R.id.nominee_image)
            val nomineeNameText: TextView = bottomSheetView.findViewById(R.id.nominee_name_text)
            val nomineeLevelText: TextView = bottomSheetView.findViewById(R.id.nominee_level_text)
            nomineeImage.setImageResource(nominee.image)
            nomineeNameText.text = nominee.name
            nomineeLevelText.text = nominee.level
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

    override fun getItemCount(): Int = nomineeList.size

    class CandidatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoImage: ImageView = itemView.findViewById(R.id.photo_image)
        private val nameText: TextView = itemView.findViewById(R.id.name_text)
        private val levelText: TextView = itemView.findViewById(R.id.level_text)

        fun bind(nominee: Nominee) {
            photoImage.setImageResource(nominee.image)
            nameText.text = nominee.name
            levelText.text = nominee.level
        }
    }
}