package com.example.medicalappointment.BenhNhan.Presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Rating(
    val doctorId: String = "",
    val doctorName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val patientName: String =""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctor: Doctor,
    navController: NavHostController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var ratings by remember { mutableStateOf<List<Rating>>(emptyList()) }

//    LaunchedEffect(Unit) {
//        FirebaseFirestore.getInstance()
//            .collection("ratedoctor")
//            .whereEqualTo("doctorId", doctor.doctorId)
//            .get()
//            .addOnSuccessListener { documents ->
//                val list = documents.mapNotNull { it.toObject(Rating::class.java) }
//                ratings = list
//            }
//    }
    LaunchedEffect(doctor.hoTen) {
        if (doctor.hoTen.isNotBlank()) {
            try {
                Log.d("DoctorDetailScreen", "Đang tải đánh giá cho bác sĩ tên: ${doctor.hoTen}")
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("ratedoctor")
                    .whereEqualTo("doctorName", doctor.hoTen)
                    .get()
                    .await()

                Log.d("DoctorDetailScreen", "Snapshot size: ${snapshot.size()}")

                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        val doctorId = doc.getString("doctorId") ?: ""
                        val doctorName = doc.getString("doctorName") ?: ""
                        val patientName = doc.getString("patientName")?: ""
                        val rating = (doc.get("rating") as? Number)?.toInt() ?: 0
                        val comment = doc.getString("comment") ?: ""
                        Rating(doctorId, doctorName, rating, comment, patientName)
                    } catch (e: Exception) {
                        Log.e("DoctorDetailScreen", "Lỗi khi đọc document: ${e.message}")
                        null
                    }
                }

                ratings = list
                Log.d("DoctorDetailScreen", "Tải thành công ${ratings.size} đánh giá")
            } catch (e: Exception) {
                Log.e("DoctorDetailScreen", "Không thể tải đánh giá: ${e.message}")
            }
        } else {
            Log.w("DoctorDetailScreen", "doctorName rỗng hoặc null, không thể tải đánh giá.")
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết bác sĩ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1E88E5))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            doctor?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(12.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = it.anh,
                                contentDescription = "Ảnh bác sĩ",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color(0xFF1E88E5), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    doctor.hoTen,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E88E5)
                                )
                                Text("Khoa: ${it.chuyenKhoa}", fontSize = 16.sp, color = Color.Gray)
                                Text(
                                    "⭐ ${it.danhGia} | ${doctor.kinhNghiem} năm kinh nghiệm",
                                    fontSize = 14.sp,
                                    color = Color(0xFF777777)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("🏥 Bệnh viện: ${doctor.diaChi}", fontSize = 16.sp, color = Color.Black)
                    Text("📞 SĐT: ${doctor.sdt}", fontSize = 16.sp, color = Color.Black)
                    Text(
                        "👥 Đã khám: ${doctor.benhNhanDaKham} bệnh nhân",
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "📝 Tiểu sử",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5)
                    )
                    Text(doctor.tieuSu, fontSize = 14.sp, color = Color.Black)

                    Spacer(Modifier.height(16.dp))


                    Button(
                        onClick = {
                            navController.navigate("bookingscreen/${doctor.doctorId}")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Đặt lịch ngay", color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "📊 Đánh giá từ bệnh nhân",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E88E5)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                if (ratings.isEmpty()) {
                    item {
                        Text("Chưa có đánh giá nào.", fontSize = 14.sp, color = Color.Gray)
                    }
                } else {
                    items(ratings) { rating ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "👤 ${rating.patientName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E88E5)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "⭐ ${rating.rating}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("💬 ${rating.comment}", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

