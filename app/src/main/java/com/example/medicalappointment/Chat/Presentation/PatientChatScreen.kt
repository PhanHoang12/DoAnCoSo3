package com.example.medicalappointment.Chat.Presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun PatientChatScreen(navController: NavHostController) {
    val receiverId = "DL1"
    val receiverName = "Bs. Trần Thị B"
    val receiverRole = "admin"

    ChatScreen(
        navController = navController,
        receiverId = receiverId,
        receiverName = receiverName,
        receiverRole = receiverRole
    )
}