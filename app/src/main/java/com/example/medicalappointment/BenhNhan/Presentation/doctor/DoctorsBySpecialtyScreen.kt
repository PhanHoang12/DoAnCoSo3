package com.example.medicalappointment.BenhNhan.Presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsBySpecialtyScreen(
    navController: NavController,
    specialtyName: String
) {
    val doctorRepository = remember { DoctorRepository() }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
//    var averageRating by remember { mutableStateOf(0f) }

    LaunchedEffect(specialtyName) {
        val allDoctors = doctorRepository.getDoctor()
        val filteredDoctors = allDoctors.filter {
            it.chuyenKhoa.equals(specialtyName, ignoreCase = true)
        }

        val doctorsWithRatings = filteredDoctors.map { doctor ->
            val avgRating = doctorRepository.calculateAverageRating(doctor.doctorId)
            doctor.copy(danhGia = avgRating)
        }

        doctors = doctorsWithRatings
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bác sĩ chuyên khoa $specialtyName",
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(doctors) { doctor ->
                DoctorItem(
                    doctor = doctor,
                    onBookingClick = {
                        navController.navigate("bookingscreen/${doctor.doctorId}")
                    },
                    onRatingClick = {
                        navController.navigate("ratedoctor/${doctor.doctorId}")
                    }
                )
            }
        }
    }
}

@Composable
fun DoctorItem(
    doctor: Doctor,
    onBookingClick: () -> Unit,
    onRatingClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = doctor.anh.ifBlank { "https://via.placeholder.com/150" },
                    contentDescription = "Ảnh bác sĩ",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.hoTen,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.MedicalInformation, contentDescription = null, tint = Color(0xFF1976D2))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kinh nghiệm: ${doctor.kinhNghiem} năm")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (doctor.danhGia != null && doctor.danhGia > 0)
                                "Đánh giá: %.1f ⭐".format(doctor.danhGia)
                            else
                                "Chưa có đánh giá"
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.MedicalInformation, contentDescription = null, tint = Color(0xFF0097A7))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chuyên khoa: ${doctor.chuyenKhoa}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFFEF6C00))
                Spacer(modifier = Modifier.width(4.dp))
                Text(doctor.diaChi)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Các nút hành động
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onRatingClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Đánh giá")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onBookingClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Đặt lịch ngay")
                }
            }
        }
    }
}
