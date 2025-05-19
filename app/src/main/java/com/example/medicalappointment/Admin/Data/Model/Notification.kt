package com.example.medicalappointment.Admin.Data.Model

data class Notification (
    val id: String ="",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    val isRead: Boolean = false
)