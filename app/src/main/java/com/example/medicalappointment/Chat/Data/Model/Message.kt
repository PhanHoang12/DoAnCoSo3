package com.example.medicalappointment.Chat.Data.Model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "text", // "text" or "image"
    val imageUrl: String? = null
)