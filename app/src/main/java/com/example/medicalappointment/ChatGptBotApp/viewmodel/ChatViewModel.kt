package com.example.medicalappointment.ChatGptBotApp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalappointment.ChatGptBotApp.data.model.ChatMessage
import com.example.medicalappointment.ChatGptBotApp.data.model.Message
import com.example.medicalappointment.ChatGptBotApp.data.model.OpenAIRequest
import com.example.medicalappointment.ChatGptBotApp.data.model.OpenAIResponse
import com.example.medicalappointment.ChatGptBotApp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val messages = mutableStateListOf<ChatMessage>()

    fun sendMessage(userMessage: String) {
        messages.add(ChatMessage(userMessage, true))

        val request = OpenAIRequest(
            messages = listOf(
                Message("system", "You are a helpful assistant."),
                Message("user", userMessage)
            )
        )


        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.createChatCompletion(request)
                val reply = response.choices.firstOrNull()?.message?.content ?: "No response"

                // Trở về Main thread để cập nhật UI
                launch(Dispatchers.Main) {
                    messages.add(ChatMessage(reply.trim(), false))
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    messages.add(ChatMessage("Error: ${e.message}", false))
                }
            }
        }
    }
}
