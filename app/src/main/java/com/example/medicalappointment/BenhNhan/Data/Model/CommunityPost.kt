package com.example.medicalappointment.BenhNhan.Data.Model

data class CommunityPost(
    val title: String,
    val description: String,
    val imageUrl: String
)

val dummyCommunityPosts = listOf(
    CommunityPost(
        title = "Chăm sóc sức khỏe mùa hè",
        description = "Những lưu ý giúp bạn khỏe mạnh trong thời tiết nắng nóng.",
        imageUrl = "https://example.com/summer_health.jpg"
    ),
    CommunityPost(
        title = "Dinh dưỡng cho người cao tuổi",
        description = "Bí quyết giữ gìn sức khỏe cho người già.",
        imageUrl = "https://example.com/elderly_nutrition.jpg"
    ),
    CommunityPost(
        title = "Tập thể dục đúng cách",
        description = "Các bài tập đơn giản phù hợp với mọi người.",
        imageUrl = "https://example.com/exercise.jpg"
    )
)
