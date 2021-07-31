package com.chibufirst.evote.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.chibufirst.evote.MainActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.adapters.NotificationsRecyclerAdapter
import com.chibufirst.evote.databinding.FragmentAdminNotificationBinding
import com.chibufirst.evote.models.Notifications
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminNotificationFragment : Fragment() {

    private var binding: FragmentAdminNotificationBinding? = null
    private lateinit var notificationsAdapter: NotificationsRecyclerAdapter
    private lateinit var demoNotifications: ArrayList<Notifications>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminNotificationBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        demoNotifications = arrayListOf(
            Notifications(
                "Welcome to this portal for the ELECTION, feel free to explore and view different positions and their candidates.",
                "12/07/2021"
            ),
            Notifications(
                "The date for this year's election will be communicated soon, get to decide those that will lead.",
                "16/07/2021"
            ),
            Notifications(
                "Please don't forget this: If you don't vote, you loose the right to complain.",
                "18/07/2021"
            ),
            Notifications("Just know that your vote counts.", "20/07/2021"),
            Notifications(
                "Welcome to this portal for the ELECTION, feel free to explore and view different positions and their candidates.",
                "12/07/2021"
            ),
            Notifications(
                "The date for this year's election will be communicated soon, get to decide those that will lead.",
                "16/07/2021"
            ),
            Notifications(
                "Please don't forget this: If you don't vote, you loose the right to complain.",
                "18/07/2021"
            ),
            Notifications("Just know that your vote counts.", "20/07/2021")
        )
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear-1}/$currentYear"
        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            headerText.setOnClickListener { requireActivity().onBackPressed() }
            notificationsRecycler.setHasFixedSize(true)
            if (demoNotifications.isEmpty()) {
                notificationInfoLayout.visibility = View.VISIBLE
                notificationsRecycler.visibility = View.GONE
            } else {
                notificationInfoLayout.visibility = View.GONE
                notificationsRecycler.visibility = View.VISIBLE
                notificationsAdapter = NotificationsRecyclerAdapter(demoNotifications)
                notificationsRecycler.adapter = notificationsAdapter
            }
            logoutImage.setOnClickListener {
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            addFab.setOnClickListener { showAddNotificationDialog() }
        }
    }

    @SuppressLint("InflateParams")
    private fun showAddNotificationDialog() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        val notificationView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_notification, null)
        val closeImage: ImageView =  notificationView.findViewById(R.id.close_image)
        val notificationEditText: EditText =  notificationView.findViewById(R.id.notification_edit_text)
        val submitButton: Button =  notificationView.findViewById(R.id.submit_button)
        materialAlertDialogBuilder.apply {
            setCancelable(false)
            setView(notificationView)
            background = getDrawable(requireContext(), R.drawable.dialog_background)
        }
        val notificationDialog: AlertDialog = materialAlertDialogBuilder.create()

        closeImage.setOnClickListener { notificationDialog.dismiss() }
        submitButton.setOnClickListener { validateInput(notificationEditText, notificationDialog) }

        notificationDialog.show()
    }

    private fun validateInput(notificationEditText: EditText, dialog: AlertDialog) {
        if (notificationEditText.text.isEmpty()) {
            displayMessage("Please enter your message.")
            notificationEditText.requestFocus()
            return
        } else {
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            demoNotifications.add(Notifications(notificationEditText.text.toString(), currentDate))
            notificationsAdapter.notifyDataSetChanged()
            displayMessage("Notification added.")
            dialog.dismiss()
        }
    }

    private fun displayMessage(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}