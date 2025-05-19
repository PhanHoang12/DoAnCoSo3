package com.example.medicalappointment.BenhNhan.Presentation.Patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.example.medicalapp.Admin.Data.Model.Doctor
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateDoctorScreen(
    navController: NavController,
    doctorId: String
) {
    var doctor by remember { mutableStateOf<Doctor?>(null) }
    var selectedRating by remember { mutableStateOf(0) }
    var isSubmitted by remember { mutableStateOf(false) }
    var comment by remember { mutableStateOf(TextFieldValue("")) }
    val coroutineScope = rememberCoroutineScope() // ✅ Add this

    LaunchedEffect(doctorId) {
        doctor = getDoctorByIdFromFirestore(doctorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đánh giá bác sĩ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        doctor?.let {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bác sĩ: ${it.hoTen}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Chọn số sao", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.Center) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { selectedRating = i },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (i <= selectedRating) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Viết đánh giá của bạn") },
                    placeholder = { Text("Ví dụ: Bác sĩ rất tận tâm...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val patientName = getCurrentPatientName()
                            val doctorName = doctor?.hoTen
                            if (patientName != null && doctorName != null) {
                                submitRatingToFirestore(
                                    doctorId = doctorId,
                                    rating = selectedRating,
                                    comment = comment.text,
                                    doctorName = doctorName,
                                    patientName = patientName
                                )
                                isSubmitted = true
                                comment = TextFieldValue("")
                                selectedRating = 0
                            }
                        }
                    },
                    enabled = selectedRating > 0 || comment.text.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Gửi đánh giá", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }

                if (isSubmitted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "🎉 Cảm ơn bạn đã đánh giá!",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


// ✅ Lấy dữ liệu bác sĩ theo documentId (chính là doctorId như "DL001")
suspend fun getDoctorByIdFromFirestore(doctorId: String): Doctor? {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("doctors")
            .document(doctorId) // dùng doctorId làm document ID
            .get()
            .await()

        snapshot.toObject(Doctor::class.java)
    } catch (e: Exception) {
        null
    }
}

// ✅ Gửi đánh giá lên Firestore
fun submitRatingToFirestore(
    doctorId: String,
    rating: Int,
    comment: String,
    doctorName: String,
    patientName: String
) {
    val ratingData = hashMapOf(
        "doctorId" to doctorId,
        "rating" to rating,
        "comment" to comment,
        "doctorName" to doctorName,
        "patientName" to patientName
    )

    FirebaseFirestore.getInstance()
        .collection("ratedoctor")
        .add(ratingData)
}

// Hàm lấy tên bệnh nhân dựa vào uid
suspend fun getCurrentPatientName(): String? {
    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return null

    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("patients")
            .document(uid)
            .get()
            .await()

        snapshot.getString("hoTen")
    } catch (e: Exception) {
        null
    }
}

