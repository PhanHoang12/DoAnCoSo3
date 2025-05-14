package com.example.medicalappointment.Chat.Presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun DoctorChatScreen(navController: NavHostController) {
    val receiverId = "DL1"
    val receiverName = "Bs. Phan Ngọc Phước"
    val receiverRole = "user"

    ChatScreen(
        navController = navController,
        receiverId = receiverId,
        receiverName = receiverName,
        receiverRole = receiverRole
    )
}

