package com.example.medicalapp.Admin.Presentation.Doctors

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository
import com.google.firebase.storage.FirebaseStorage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar
import com.example.medicalappointment.Admin.Presentation.Hospital.AddHospitalScreen

@Composable
fun AddDoctorScreen(
    navController: NavController,
    viewModel: DoctorViewModel = viewModel(factory = DoctorViewModelFactory(DoctorRepository())
    )) {
    val context = LocalContext.current
    val message by viewModel.message.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    var doctorId by remember { mutableStateOf("") }
    var hoTen by remember { mutableStateOf("") }
    var chuyenKhoa by remember { mutableStateOf("") }
    var noiCongTac by remember { mutableStateOf("") }
    var tieuSu by remember { mutableStateOf("") }
    var kinhNghiem by remember { mutableStateOf("") }
    var danhGia by remember { mutableStateOf("") }
    var benhNhanDaKham by remember { mutableStateOf("") }
    var sdt by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            AdminTopBar(userName = "Admin") {
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Thêm bác sĩ", style = MaterialTheme.typography.bodyLarge)

            OutlinedTextField(
                value = doctorId,
                onValueChange = { doctorId = it },
                label = { Text("Mã bác sĩ") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = hoTen,
                onValueChange = { hoTen = it },
                label = { Text("Họ tên") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = chuyenKhoa,
                onValueChange = { chuyenKhoa = it },
                label = { Text("Chuyên khoa") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = noiCongTac,
                onValueChange = { noiCongTac = it },
                label = { Text("Nơi công tác") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tieuSu,
                onValueChange = { tieuSu = it },
                label = { Text("Tiểu sử") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = kinhNghiem,
                onValueChange = { kinhNghiem = it },
                label = { Text("Kinh nghiệm (năm)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = danhGia,
                onValueChange = { danhGia = it },
                label = { Text("Đánh giá (0-5)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = benhNhanDaKham,
                onValueChange = { benhNhanDaKham = it },
                label = { Text("Bệnh nhân đã khám") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sdt,
                onValueChange = { sdt = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )

            if (selectedImageUri == null) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {}, // Không cho thay đổi giá trị
                    label = { Text("Ảnh bệnh viện") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { pickImageLauncher.launch("image/*") }, // Bấm vào cả ô để chọn ảnh
                    readOnly = true,
                    //enabled = false, // Vô hiệu hóa nhập liệu
                    trailingIcon = {
                        IconButton(
                            onClick = { pickImageLauncher.launch("image/*") }
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Chọn ảnh")
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Ảnh bệnh viện",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                    IconButton(
                        onClick = { selectedImageUri = null }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa ảnh")
                    }
                }
            }

            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    // Kiểm tra các trường input
                    if (doctorId.isBlank() || hoTen.isBlank() || chuyenKhoa.isBlank() || kinhNghiem.isBlank() || danhGia.isBlank()) {
                        Toast.makeText(
                            context,
                            "Vui lòng điền đầy đủ thông tin bác sĩ!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // Kiểm tra kinh nghiệm và đánh giá
                    val kinhNghiemValue = kinhNghiem.toIntOrNull() ?: 0
                    val danhGiaValue = danhGia.toDoubleOrNull()?.coerceIn(0.0, 5.0) ?: 0.0

                    // Tạo đối tượng BacSi
                    val bacSi = Doctor(
                        doctorId = doctorId,
                        hoTen = hoTen,
                        chuyenKhoa = chuyenKhoa,
                        diaChi = noiCongTac,
                        tieuSu = tieuSu,
                        kinhNghiem = kinhNghiemValue,
                        danhGia = danhGiaValue,
                        benhNhanDaKham = benhNhanDaKham.toIntOrNull() ?: 0,
                        sdt = sdt,
                        anh = "", // Ảnh sẽ được cập nhật sau khi upload xong
                        website = website
                    )

                    // Upload ảnh bác sĩ lên Firebase Storage và lưu vào Firestore
                    selectedImageUri?.let { uri ->
                        val storageReference =
                            FirebaseStorage.getInstance().reference.child("doctor_images/${doctorId}")
                        val uploadTask = storageReference.putFile(uri)
                        uploadTask.addOnSuccessListener {
                            storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                                val updatedBacSi = bacSi.copy(anh = downloadUrl.toString())
                                viewModel.saveDoctor(updatedBacSi)
                                navController.popBackStack()
                            }
                        }
                        uploadTask.addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Upload ảnh thất bại: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: run {
                        // Nếu không có ảnh, vẫn lưu bác sĩ mà không có ảnh
                        viewModel.saveDoctor(bacSi)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu bác sĩ")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewDoctorScreen() {
    val navController = rememberNavController()
    AddDoctorScreen(navController = navController)
}
