package com.chibufirst.evote.models

import java.io.Serializable

data class Student(
    var user: String? = null,
    var fullName: String? = null,
    var regno: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var program: String? = null,
    var level: String? = null,
    var photo: String? = null,
    var position: String? = null,
    var bio: String? = null,
    var approve: Boolean? = null,
    var voted: Boolean? = null
) : Serializable
