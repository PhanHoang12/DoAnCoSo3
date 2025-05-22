package com.example.medicalappointment.Bacsi.Presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.medicalappointment.BenhNhan.Presentation.Patient.Booking
import com.example.medicalappointment.BenhNhan.Presentation.booking.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun DoctorScreen(
    navController: NavHostController,
    doctorId: String,
    viewModel: BookingViewModel = viewModel()
) {
    val bookings by viewModel.bookingList
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }


    LaunchedEffect(Unit) {
        viewModel.loadBookings(doctorId)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Lí do từ chối của doctor
        if (showDialog && selectedBooking != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    rejectionReason = ""
                    selectedBooking = null
                },
                title = {
                    Text("Nhập lý do từ chối")
                },
                text = {
                    androidx.compose.material3.OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        label = { Text("Lý do") },
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedBooking?.let {
                                viewModel.rejectBooking(
                                    it.BookingID,
                                    it.IdBenhNhan,
                                    rejectionReason,
                                    onSuccess = {
                                        Toast.makeText(context, "Đã từ chối lịch hẹn", Toast.LENGTH_SHORT).show()
                                        showDialog = false
                                        rejectionReason = ""
                                        selectedBooking = null
                                    },
                                    onFailure = { errorMsg ->
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    ) {
                        Text("Gửi")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            rejectionReason = ""
                            selectedBooking = null
                        }
                    ) {
                        Text("Huỷ")
                    }
                }
            )
        }

        DoctorTopBar(doctorId = doctorId, onMessageClick = {
            // TODO: mở màn hình chat
        },
        onProfileClick = {
            navController.navigate("doctorProfile/$doctorId")
        })
        LazyColumn(modifier = Modifier
            .padding(16.dp)
            .weight(1f)  // Fill the available space
        ) {
            // Sắp xếp theo ngày + giờ (kiểm tra kiểu dữ liệu của ngày và giờ trước khi sắp xếp)
            val sortedBookings = bookings.sortedWith(
                compareBy({ it.ngayKham }, { it.gioKham })
            )

            items(sortedBookings) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "👤 Bệnh nhân: ${booking.hoTen}")
                        Text(text = "📅 Ngày khám: ${booking.ngayKham}")
                        Text(text = "⏰ Giờ khám: ${booking.gioKham}")
                        Text(text = "📞 SĐT: ${booking.soDienThoai}")
                        Text(text = "📌 Trạng thái: ${booking.trangThai}")
                        if(booking.trangThai != "Đã xác nhận" && booking.trangThai!= "Đã từ chối") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Xác nhận lịch hẹn
                                IconButton(onClick = {
                                    viewModel.confirmBooking(
                                        booking.BookingID,
                                        booking.IdBenhNhan,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Đã xác nhận lịch hẹn!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onFailure = { errorMsg ->
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    )
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Xác nhận",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                // Từ chối lịch hẹn
                                IconButton(onClick = {
//                                viewModel.rejectBooking(
//                                    booking.BookingID,
//                                    booking.IdBenhNhan,
//                                    onSuccess = {
//                                        Toast.makeText(context, "Đã từ chối lịch hẹn", Toast.LENGTH_SHORT).show()
//                                    },
//                                    onFailure = { errorMsg ->
//                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
//                                    }
//                                )
                                    selectedBooking = booking
                                    showDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Từ chối",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("signin") {
                    popUpTo("homeDoctor/{doctorId}") { inclusive = true }
                    Toast.makeText(context, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
        ) {
            Text("Đăng xuất", style = MaterialTheme.typography.titleMedium)
        }

    }
}
