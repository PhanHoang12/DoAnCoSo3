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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar
import com.example.medicalappointment.Admin.Presentation.Home.DrawerItem
import com.example.medicalappointment.Admin.Presentation.Hospital.AddHospitalScreen
import com.example.medicalappointment.Admin.Presentation.Specialty.SpecialtyDropdown
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.medicalapp.Admin.Data.Model.Specialty
import com.example.medicalapp.Admin.Presentation.Specialty.SpecialtyViewModel
import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository
import com.example.medicalappointment.Admin.Presentation.Specialty.SpecialtyViewModelFactory


@Composable
fun AddDoctorScreen(
    navController: NavController,
    viewModel: DoctorViewModel = viewModel(factory = DoctorViewModelFactory(DoctorRepository())),
    specialtyViewModel: SpecialtyViewModel = viewModel(factory = SpecialtyViewModelFactory(
        SpecialtyRepository()))
) {
    val context = LocalContext.current
    val message by viewModel.message.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    var doctorId by remember { mutableStateOf("") }
    var hoTen by remember { mutableStateOf("") }
//    var chuyenKhoa by remember { mutableStateOf("") }
    var chuyenKhoa by remember { mutableStateOf<Specialty?>(null) }
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val specialties by specialtyViewModel.specialtys.collectAsState()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                Divider()
                DrawerItem("Bệnh viện") { navController.navigate("home_hospital") }
                DrawerItem("Bác sĩ") { navController.navigate("home_doctor") }
                DrawerItem("Bệnh nhân") { navController.navigate("home_patient") }
                DrawerItem("Chuyên khoa") { navController.navigate("home_specialty") }
                DrawerItem("Phân quyền") { navController.navigate("decentralization") }
                DrawerItem("Quản lý lịch hẹn") { /* navController.navigate("appointment_screen") */ }
                DrawerItem("Đăng xuất") {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate("signin") {
                        popUpTo("admin_home") { inclusive = true }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                AdminTopBar(userName = "Admin") {
                    scope.launch { drawerState.open() }  // Mở Drawer khi nhấn menu

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
//                OutlinedTextField(
//                    value = chuyenKhoa,
//                    onValueChange = { chuyenKhoa = it },
//                    label = { Text("Chuyên khoa") },
//                    modifier = Modifier.fillMaxWidth()
//                )
                SpecialtyDropdown(
                    specialties = specialties,
                    selectedSpecialty = chuyenKhoa,
                    onSpecialtySelected = { chuyenKhoa = it }
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
                        label = { Text("Ảnh bác sĩ") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pickImageLauncher.launch("image/*") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { pickImageLauncher.launch("image/*") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Chọn ảnh"
                                )
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
                        if (doctorId.isBlank() || hoTen.isBlank() || kinhNghiem.isBlank() || danhGia.isBlank()) {
                            Toast.makeText(
                                context,
                                "Vui lòng điền đầy đủ thông tin bác sĩ!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val kinhNghiemValue = kinhNghiem.toIntOrNull()
                        val benhNhanValue = benhNhanDaKham.toIntOrNull()
                        val danhGiaValue = danhGia.toDoubleOrNull()?.coerceIn(0.0, 5.0) ?: 0.0
                        if (kinhNghiemValue == null || benhNhanValue == null) {
                            Toast.makeText(
                                context,
                                "Vui lòng nhập đúng định dạng số cho kinh nghiệm và bệnh nhân đã khám!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }



                        // Tạo đối tượng BacSi
                        val bacSi = Doctor(
                            doctorId = doctorId,
                            hoTen = hoTen,
                            chuyenKhoa = chuyenKhoa?.name?: "",
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
                            viewModel.saveDoctor(bacSi)
                            navController.popBackStack()
//                            Toast.makeText(context, "Thêm bác sĩ thành công!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lưu bác sĩ")
                }
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
