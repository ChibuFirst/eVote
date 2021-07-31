package com.chibufirst.evote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chibufirst.evote.R
import com.chibufirst.evote.models.Notifications

class NotificationsRecyclerAdapter(private val notificationsList: ArrayList<Notifications>) :
    RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationRecyclerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationRecyclerViewHolder {
        return NotificationRecyclerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationRecyclerViewHolder, position: Int) {
        val notifications = notificationsList[position]
        holder.bind(notifications)
    }

    override fun getItemCount(): Int = notificationsList.size

    class NotificationRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val notificationMessageText: TextView =
            itemView.findViewById(R.id.notification_message_text)
        private val notificationDateText: TextView =
            itemView.findViewById(R.id.notification_date_text)

        fun bind(notifications: Notifications) {
            notificationMessageText.text = notifications.message
            notificationDateText.text = notifications.date
        }
    }
}