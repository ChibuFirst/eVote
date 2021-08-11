package com.chibufirst.evote.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.chibufirst.evote.MainActivity
import com.chibufirst.evote.R
import com.chibufirst.evote.adapters.NotificationsRecyclerAdapter
import com.chibufirst.evote.databinding.FragmentAdminNotificationBinding
import com.chibufirst.evote.models.Notifications
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AdminNotificationFragment : Fragment() {

    private var binding: FragmentAdminNotificationBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var firestoreListener: ListenerRegistration
    private lateinit var notificationsAdapter: NotificationsRecyclerAdapter

    companion object {
        private val TAG: String = AdminNotificationFragment::class.java.simpleName
    }

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

        auth = Firebase.auth
        db = Firebase.firestore

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentSession = "${currentYear - 1}/$currentYear"

        binding!!.apply {
            titleText.text = getString(R.string.elections, currentSession)
            headerText.setOnClickListener { requireActivity().onBackPressed() }
            notificationsRecycler.setHasFixedSize(true)
            logoutImage.setOnClickListener {
                auth.signOut()
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
            addFab.setOnClickListener { showAddNotificationDialog() }
        }

        db.collection(Constants.NOTIFICATIONS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get(Source.CACHE)
            .addOnCompleteListener { task ->
                val notificationsList = arrayListOf<Notifications>()
                if (task.isSuccessful) {
                    for (doc in task.result!!) {
                        val notifications = doc.toObject<Notifications>()
                        notificationsList.add(notifications)
                    }
                }
                binding!!.apply {
                    if (notificationsList.isEmpty()) {
                        notificationInfoLayout.visibility = View.VISIBLE
                        notificationsRecycler.visibility = View.GONE
                    } else {
                        notificationInfoLayout.visibility = View.GONE
                        notificationsRecycler.visibility = View.VISIBLE
                        notificationsAdapter = NotificationsRecyclerAdapter(notificationsList)
                        notificationsRecycler.adapter = notificationsAdapter
                    }
                }
            }

        firestoreListener = db.collection(Constants.NOTIFICATIONS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(EventListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed!", error)
                    return@EventListener
                }

                val notifyList = arrayListOf<Notifications>()
                for (doc in value!!) {
                    val notifications = doc.toObject<Notifications>()
                    notifyList.add(notifications)
                }
                binding!!.apply {
                    if (notifyList.isEmpty()) {
                        notificationInfoLayout.visibility = View.VISIBLE
                        notificationsRecycler.visibility = View.GONE
                    } else {
                        notificationInfoLayout.visibility = View.GONE
                        notificationsRecycler.visibility = View.VISIBLE
                        notificationsAdapter = NotificationsRecyclerAdapter(notifyList)
                        notificationsRecycler.adapter = notificationsAdapter
                    }
                }
            })
    }

    @SuppressLint("InflateParams")
    private fun showAddNotificationDialog() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        val notificationView =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_notification, null)
        val closeImage: ImageView = notificationView.findViewById(R.id.close_image)
        val notificationEditText: EditText =
            notificationView.findViewById(R.id.notification_edit_text)
        val submitButton: Button = notificationView.findViewById(R.id.submit_button)
        materialAlertDialogBuilder.apply {
            setCancelable(false)
            setView(notificationView)
            background = getDrawable(requireContext(), R.drawable.dialog_background)
        }
        val notificationDialog: AlertDialog = materialAlertDialogBuilder.create()

        closeImage.setOnClickListener { notificationDialog.dismiss() }
        submitButton.setOnClickListener {
            Util.hideKeyboard(requireActivity())
            submitButton.isEnabled = false
            validateInput(submitButton, notificationEditText, notificationDialog)
        }

        notificationDialog.show()
    }

    private fun validateInput(button: Button, notificationEditText: EditText, dialog: AlertDialog) {
        if (notificationEditText.text.isEmpty()) {
            Util.displayLongMessage(requireContext(), "Please enter your message.")
            notificationEditText.requestFocus()
            return
        } else {
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val notifications = Notifications(
                notificationEditText.text.toString(), currentDate, Timestamp(
                    Date()
                )
            )
            db.collection(Constants.NOTIFICATIONS)
                .add(notifications)
                .addOnSuccessListener {
                    Util.displayShortMessage(requireContext(), "Notification added.")
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Util.displayLongMessage(
                        requireContext(),
                        "Error: ${e.message}"
                    )
                }
        }
        button.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener.remove()
        binding = null
    }

}