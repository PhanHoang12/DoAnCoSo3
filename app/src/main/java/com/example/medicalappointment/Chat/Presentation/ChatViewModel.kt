package com.example.medicalappointment.Chat.Presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalappointment.Chat.Data.Model.Message
import com.example.medicalappointment.Chat.Data.Reponsitory.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    fun loadMessages(senderId: String, receiverId: String) {
        viewModelScope.launch {
            repository.getMessages(senderId, receiverId).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(senderId: String, receiverId: String, content: String) {
        val message = Message(

            senderId = senderId,
            receiverId = receiverId,
            content = content,
            type = "text"
        )
        repository.sendMessage(message) {}
    }

    fun sendImageMessage(senderId: String, receiverId: String, imageUri: Uri) {
        viewModelScope.launch {
            val url = repository.uploadImage(imageUri)
            val message = Message(
                senderId = senderId,
                receiverId = receiverId,
                content = "",
                type = "image",
                imageUrl = url
            )
            repository.sendMessage(message) {}
        }
    }
}
