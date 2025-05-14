package com.example.medicalappointment.Chat.Presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ChatScreen(
    navController: NavHostController,
    receiverName: String,
    receiverRole: String,
    receiverId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val messages by viewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadMessages(senderId, receiverId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                if (message.type == "text") {
                    Text(text = message.content)
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(message.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                viewModel.sendMessage(senderId, receiverId, messageText)
                messageText = ""
            }) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }
}
