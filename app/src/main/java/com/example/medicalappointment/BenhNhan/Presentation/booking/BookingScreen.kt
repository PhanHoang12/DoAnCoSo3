package com.example.medicalapp.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.medicalapp.BenhNhan.Repository.PatientRepository
import com.example.medicalappointment.BenhNhan.Data.Model.Patient
import com.example.medicalappointment.BenhNhan.Data.Repository.BookingRepository
import java.text.SimpleDateFormat
@Composable
fun BookingScreen(navController: NavHostController, doctorId: String) {
    val repo = remember { BookingRepository() }
    val bookingInfo by repo.bookingInfo.collectAsState()
    val doctor by repo.doctor.collectAsState()

    val availableDates = remember { repo.getAvailableDates() }
    val availableTimes = remember(bookingInfo.selectedPeriod) { repo.getTimes(bookingInfo.selectedPeriod) }

    val patientRepo = remember { PatientRepository() }
    var patient by remember { mutableStateOf<Patient?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy") }

    LaunchedEffect(Unit) {
        val result = patientRepo.getPatientInfo()
        if (result.isSuccess) {
            patient = result.getOrNull()
        }
    }

    LaunchedEffect(doctorId) {
        repo.fetchDoctorById(doctorId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút quay lại
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(top = 16.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color(0xFF1976D2)
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Điều chỉnh giá trị này tùy theo mong muốn của bạn


        Text("Đặt lịch khám", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        StepIndicatorBookingConfirm(currentStep = 1)
        Spacer(modifier = Modifier.height(16.dp))

        // Doctor Card
        doctor?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = it.anh,
                        contentDescription = "Doctor Avatar",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Tiến sĩ, Bác sĩ", fontSize = 14.sp, color = Color.Gray)
                        Text(it.hoTen, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Text("Chuyên khoa: ${it.chuyenKhoa}", fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }
        } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(12.dp))

        // Patient Info
        if (patient != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Thông tin bệnh nhân", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF6C00))
                    Spacer(modifier = Modifier.height(8.dp))
                    RowInfo(label = "Họ tên:", value = patient!!.hoTen)
                    RowInfo(label = "Ngày sinh:", value = dateFormat.format(patient!!.ngaysinh))
                    RowInfo(label = "Giới tính:", value = if (patient!!.gioitinh) "Nam" else "Nữ")
                    RowInfo(label = "Số điện thoại:", value = patient!!.sdt)
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Chọn ngày khám", fontWeight = FontWeight.SemiBold)

        LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
            items(availableDates) { date ->
                val isSelected = bookingInfo.selectedDate == date
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(64.dp)
                        .background(
                            if (isSelected) Color(0xFF1976D2) else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { repo.updateDate(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(date.dayOfMonth.toString(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                        Text(date.dayOfWeek.name.take(3), color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { repo.updatePeriod("morning") }, modifier = Modifier.weight(1f)) { Text("Sáng") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { repo.updatePeriod("afternoon") }, modifier = Modifier.weight(1f)) { Text("Chiều") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Chọn giờ (${if (bookingInfo.selectedPeriod == "morning") "Sáng" else "Chiều"})", fontWeight = FontWeight.Bold)

        LazyRow(modifier = Modifier.padding(top = 8.dp)) {
            items(availableTimes) { time ->
                val isSelected = bookingInfo.selectedTime == time
                TimeBox(time = time, isSelected = isSelected) {
                    repo.updateTime(time)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                navController.navigate("bookingconfirmscreen/${bookingInfo.selectedDate}/${bookingInfo.selectedTime}/${doctorId}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tiếp tục")
        }
    }
}


@Composable
fun RowInfo(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(value)
    }
}

@Composable
fun TimeBox(time: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                color = if (isSelected) Color(0xFF1976D2) else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(time, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StepIndicatorBookingConfirm(currentStep: Int) {
    val steps = listOf("Chọn lịch khám", "Xác nhận", "Nhận lịch hẹn")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        steps.forEachIndexed { index, label ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (index + 1 == currentStep) Color(0xFF1976D2) else Color(0xFFB0BEC5),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (index + 1 == currentStep) FontWeight.Bold else FontWeight.Normal,
                        color = if (index + 1 == currentStep) Color(0xFF1976D2) else Color.DarkGray
                    )
                }
                if (index < steps.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}
