package com.example.medicalappointment.ChatGptBotApp.data.model

data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)