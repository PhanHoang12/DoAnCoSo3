package com.example.medicalapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository
import com.example.medicalapp.Admin.Presentation.Doctors.DoctorViewModel
import com.example.medicalapp.Admin.Presentation.Doctors.DoctorViewModelFactory
import com.example.medicalapp.BenhNhan.Repository.PatientRepository
import com.example.medicalapp.presentation.RowInfo
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import android.widget.Toast
import com.example.medicalappointment.BenhNhan.Data.Model.Patient

@Composable
fun BookingConfirmScreen(navController: NavHostController, date: LocalDate, time: String, doctorId: String) {
    val context = LocalContext.current
    val patientRepo = remember { PatientRepository() }
    var patient by remember { mutableStateOf<Patient?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy") }

    val doctorViewModel: DoctorViewModel = viewModel(
        factory = DoctorViewModelFactory(DoctorRepository())
    )
    val selectedDoctor by doctorViewModel.selectedDoctor.collectAsState()

//    val bookingCode = "bookingID" + System.currentTimeMillis().toString()
    val docRef = FirebaseFirestore.getInstance().collection("bookings").document()
    val bookingId = docRef.id
    LaunchedEffect(doctorId) {
        doctorViewModel.fetchDoctorById(doctorId)
    }

    LaunchedEffect(Unit) {
        val result = patientRepo.getPatientInfo()
        if (result.isSuccess) {
            patient = result.getOrNull()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tiêu đề "Xác nhận đặt lịch" lên trên đầu
        Text(
            "Xác nhận đặt lịch",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp) // Đẩy tiêu đề xuống 1 chút
        )

        Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách sau tiêu đề

        StepIndicatorBookingConfirm(currentStep = 2)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Thông tin bệnh nhân", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

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
            Text("Đang tải thông tin bệnh nhân...", modifier = Modifier.align(Alignment.CenterHorizontally))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Thông tin khám", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RowInfoBookingConfirm("Ngày khám:", "${date.dayOfMonth}/${date.monthValue}/${date.year}")
                RowInfoBookingConfirm("Giờ khám:", time)
                RowInfoBookingConfirm("Tên bác sĩ", selectedDoctor?.hoTen ?: "Đang tải ...")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val db = FirebaseFirestore.getInstance()
                val selectedDate = "${date.dayOfMonth}/${date.monthValue}/${date.year}"

                db.collection("bookings")
                    .whereEqualTo("ngayKham", selectedDate)
                    .whereEqualTo("gioKham", time)
                    .whereEqualTo("IdBacSi", doctorId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            Toast.makeText(context, "Lịch này đã được đặt trước, vui lòng chọn khung giờ khác.", Toast.LENGTH_LONG).show()
                        } else {
                            val bookingData = hashMapOf(
                                "hoTen" to patient?.hoTen,
                                "gioiTinh" to if (patient?.gioitinh == true) "Nam" else "Nữ",
                                "ngaySinh" to dateFormat.format(patient?.ngaysinh),
                                "soDienThoai" to patient?.sdt,
                                "ngayKham" to selectedDate,
                                "TenBacSi" to selectedDoctor?.hoTen,
                                "IdBacSi" to doctorId,
                                "IdBenhNhan" to patient?.userId,
                                "gioKham" to time,
                                "bookingID" to bookingId,
                                "trangThai" to "Chưa xác nhận"

                            )

                            db.collection("bookings")
                                .document(bookingId)
                                .set(bookingData)
                                .addOnSuccessListener {
                                    navController.navigate("successscreen")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("BookingConfirmScreen", "Lỗi lưu lịch khám", e)
                                    Toast.makeText(context, "Lỗi lưu lịch khám", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("BookingConfirmScreen", "Lỗi kiểm tra lịch trùng", e)
                        Toast.makeText(context, "Lỗi kiểm tra lịch trùng", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xác nhận đặt lịch")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("bookingscreen/$doctorId")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Quay lại")
        }
    }
}


@Composable
fun RowInfoBookingConfirm(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value)
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

@Preview(showBackground = true)
@Composable
fun PreviewBookingConfirmScreen() {
    BookingConfirmScreen(
        navController = rememberNavController(),
        date = LocalDate.now(),
        time = "09:00",
        doctorId = "1"
    )
}
