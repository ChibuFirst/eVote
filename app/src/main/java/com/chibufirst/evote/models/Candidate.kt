package com.chibufirst.evote.models

data class Candidate(
    val fullName: String,
    val photo: String,
    val regno: String,
    val gender: String,
    val program: String,
    val level: String,
    val position: String,
    val bio: String,
    val password: String
)
