package com.example.medicalappointment.BenhNhan.Presentation.Patient

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Booking(
    var hoTen: String = "",
    var gioiTinh: String = "",
    var ngaySinh: String = "",
    var soDienThoai: String = "",
    var ngayKham: String = "",
    var gioKham: String = "",
    var IdBenhNhan: String = "",
    var IdBacSi: String = "",
    var TenBacSi: String = "",
    var BookingID: String ="",
    var documentId: String = "",
    var trangThai: String = ""

    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Scheduled(navController: NavHostController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""

    var allBookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    var pastBookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var upcomingBookings by remember { mutableStateOf<List<Booking>>(emptyList()) }

    fun Booking.toDateTimeSafe(): LocalDateTime? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy H:mm")
            LocalDateTime.parse("$ngayKham $gioKham", formatter)
        } catch (e: Exception) {
            Log.e("ScheduledScreen", "Parse error: $ngayKham $gioKham", e)
            null
        }
    }

    LaunchedEffect(userId) {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("bookings")
                .whereEqualTo("IdBenhNhan", userId)
                .get()
                .await()

            allBookings = snapshot.documents.mapNotNull { doc ->
                val booking = doc.toObject(Booking::class.java)
                booking?.copy(documentId = doc.id)
            }

            // Lọc lịch đã qua
            val now = LocalDateTime.now()
            pastBookings = allBookings.filter {
                val dateTime = it.toDateTimeSafe()
                Log.d("TimeParse", "Parsed: $dateTime from ${it.ngayKham} ${it.gioKham}")
                dateTime?.isBefore(now) == true
            }

            // Lọc lịch sắp tới
            upcomingBookings = allBookings.filter {
                val dateTime = it.toDateTimeSafe()
                dateTime?.isAfter(now) == true
            }

        } catch (e: Exception) {
            Log.e("ScheduledScreen", "Lỗi khi tải lịch đặt", e)
        } finally {
            isLoading = false
        }
    }

    suspend fun deleteBooking(booking: Booking) {
        try {
            FirebaseFirestore.getInstance()
                .collection("bookings")
                .document(booking.documentId)
                .delete()
                .await()

            allBookings = allBookings.filter { it.documentId != booking.documentId }
            pastBookings = pastBookings.filter { it.documentId != booking.documentId }
            upcomingBookings = upcomingBookings.filter { it.documentId != booking.documentId }

            Log.d("ScheduledScreen", "Đã xóa lịch hẹn thành công!")
        } catch (e: Exception) {
            Log.e("ScheduledScreen", "Lỗi khi xóa lịch hẹn", e)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Lịch đã đặt", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                pastBookings.isEmpty() && upcomingBookings.isEmpty() -> {
                    Text(
                        text = "Không có lịch hẹn nào.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        // Hiển thị lịch sắp tới
                        item {
                            Text(
                                text = "Lịch hẹn sắp tới",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(upcomingBookings) { booking ->
                            BookingItem(
                                booking = booking,
                                onDelete = {
                                    coroutineScope.launch {
                                        deleteBooking(booking)
                                    }
                                }
                            )
                        }

                        // Hiển thị lịch đã qua
                        item {
                            Text(
                                text = "Lịch hẹn đã qua",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(pastBookings) { booking ->
                            BookingItem(
                                booking = booking,
                                onDelete = {
                                    coroutineScope.launch {
                                        deleteBooking(booking)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "👤 Bệnh nhân: ${booking.hoTen}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "📅 Ngày khám: ${booking.ngayKham}", fontSize = 14.sp)
            Text(text = "🕒 Giờ khám: ${booking.gioKham}", fontSize = 14.sp)
            Text(text = "📞 SĐT: ${booking.soDienThoai}", fontSize = 14.sp)
            Text(text = "🩺 Bác sĩ: ${booking.TenBacSi}", fontSize = 14.sp)
            Text(text = "🆔 Mã lịch hẹn: ${booking.BookingID}", fontSize = 14.sp)
            Text(text = "📌 Trạng thái: ${booking.trangThai}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            if (booking.trangThai == "Chưa xác nhận") {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Xóa lịch hẹn", color = Color.White)
                }
            }
        }
    }
}

