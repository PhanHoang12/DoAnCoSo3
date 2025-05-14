package com.example.medicalappointment.Bacsi.Presentation

    import androidx.compose.foundation.Image
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.unit.dp
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Message
    import coil.compose.rememberAsyncImagePainter
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.ktx.toObject
    import com.example.medicalapp.Admin.Data.Model.Doctor

    @Composable
    fun DoctorTopBar(
        doctorId: String,
        onMessageClick: () -> Unit,
        onProfileClick: () -> Unit
    ) {
        var doctorName by remember { mutableStateOf("") }
        var doctorImageUrl by remember { mutableStateOf("") }

        // Lấy dữ liệu từ Firestore
        LaunchedEffect(doctorId) {
            FirebaseFirestore.getInstance()
                .collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener { doc ->
                    val doctor = doc.toObject<Doctor>()
                    doctorName = doctor?.hoTen ?: "Không rõ"
                    doctorImageUrl = doctor?.anh ?: ""
                }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = if (doctorImageUrl.isNotEmpty()) doctorImageUrl else "https://via.placeholder.com/150"
                    ),
                    contentDescription = "Ảnh bác sĩ",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable{onProfileClick()}
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Xin chào,", style = MaterialTheme.typography.bodySmall)
                    Text(text = doctorName, style = MaterialTheme.typography.titleMedium)
                }
            }
            IconButton(onClick = onMessageClick) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Tin nhắn",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
