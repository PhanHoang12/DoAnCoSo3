package com.example.medicalappointment.ChatGptBotApp.network

import com.example.medicalappointment.ChatGptBotApp.data.model.OpenAIRequest
import com.example.medicalappointment.ChatGptBotApp.data.model.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApi {
    @Headers(
        "Content-Type: application/json",
        "HTTP-Referer: https://localhost", // bắt buộc phải có
        "X-Title: Medical" // tên gợi nhớ cho key, có thể đặt tùy
    )
    @POST("api/v1/chat/completions")
    suspend fun createChatCompletion(@Body request: OpenAIRequest): OpenAIResponse
}
