package com.ack.ststephenskayo

data class UserModel(
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val dateJoined: String = "",
    val total_welfare_paid: Double = 0.0,
    val total_twenty_paid: Double = 0.0
)
