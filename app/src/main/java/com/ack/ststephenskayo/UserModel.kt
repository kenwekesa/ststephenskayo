package com.ack.ststephenskayo

data class UserModel(
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val dateJoined: String = "",
    val birthday: String = "",
    val fellowship: String = "",
    val memberNumber: String ="",
    val birthDate: String = "",
    val birthMonth: String = "",
    val fieldOfStudy: String ="",
    val total_welfare_paid: Double = 0.0,
    val total_twenty_paid: Double = 0.0,
    val twenty_opening_bal: Double = 0.0,
    val welfare_opening_bal:Double = 0.0
)
