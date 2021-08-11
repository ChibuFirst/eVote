package com.chibufirst.evote.models

import com.google.firebase.Timestamp

data class Notifications(
    val message: String? = null,
    val date: String? = null,
    val timestamp: Timestamp? = null
)
