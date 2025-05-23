package com.example.medicalappointment.Bacsi.Presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.example.medicalapp.Admin.Data.Model.Specialty
import com.example.medicalapp.Admin.Presentation.Specialty.SpecialtyViewModel
import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository
import com.example.medicalappointment.Admin.Presentation.Specialty.SpecialtyDropdown
import com.example.medicalappointment.Admin.Presentation.Specialty.SpecialtyViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@Composable
fun DoctorProfileScreen(
    doctorId: String,
    navController: NavHostController,
    specialtyViewModel: SpecialtyViewModel = viewModel(factory = SpecialtyViewModelFactory(
        SpecialtyRepository()))
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var hoTen by remember { mutableStateOf("") }
    var chuyenKhoa by remember { mutableStateOf<Specialty?>(null) }
    var diaChi by remember { mutableStateOf("") }
    var tieuSu by remember { mutableStateOf("") }
    var kinhNghiem by remember { mutableStateOf("") }
    var danhGia by remember { mutableStateOf("") }
    var benhNhanDaKham by remember { mutableStateOf("") }
    var sdt by remember { mutableStateOf("") }
    var anh by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var uploading by remember { mutableStateOf(false) }

    val specialties by specialtyViewModel.specialtys.collectAsState()


    LaunchedEffect(doctorId, specialties) {
        if (specialties.isNotEmpty()) {
            db.collection("doctors").document(doctorId).get().addOnSuccessListener { doc ->
                val doctor = doc.toObject(Doctor::class.java)
                doctor?.let {
                    hoTen = it.hoTen
//                chuyenKhoa = it.chuyenKhoa
                    chuyenKhoa = specialties.find { sp -> sp.name == it.chuyenKhoa }
                    diaChi = it.diaChi
                    tieuSu = it.tieuSu
                    kinhNghiem = it.kinhNghiem.toString()
                    danhGia = it.danhGia.toString()
                    benhNhanDaKham = it.benhNhanDaKham.toString()
                    sdt = it.sdt
                    anh = it.anh
                    website = it.website
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploading = true
            val ref = storage.reference.child("doctors/${UUID.randomUUID()}.jpg")
            ref.putFile(it).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                    anh = downloadUrl.toString()
                    uploading = false
                }
            }.addOnFailureListener {
                uploading = false
                Toast.makeText(context, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(4.dp)
                .height(48.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Quay lại", style = MaterialTheme.typography.labelLarge)
        }

//        Text("Hồ sơ bác sĩ", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberAsyncImagePainter(model = anh.ifEmpty { "https://via.placeholder.com/150" }),
            contentDescription = "Ảnh bác sĩ",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { launcher.launch("image/*") }
                .align(Alignment.CenterHorizontally)
        )

        if (uploading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField("Họ tên", hoTen) { hoTen = it }
//        ProfileTextField("Chuyên khoa", chuyenKhoa) { chuyenKhoa = it }
        SpecialtyDropdown(
            specialties = specialties,
            selectedSpecialty = chuyenKhoa,
            onSpecialtySelected = { chuyenKhoa = it }
        )
        ProfileTextField("Địa chỉ", diaChi) { diaChi = it }
        ProfileTextField("Tiểu sử", tieuSu, singleLine = false) { tieuSu = it }
        ProfileTextField("Kinh nghiệm (năm)", kinhNghiem) { kinhNghiem = it }
        ProfileTextField("Đánh giá", danhGia) { danhGia = it }
        ProfileTextField("Bệnh nhân đã khám", benhNhanDaKham) { benhNhanDaKham = it }
        ProfileTextField("Số điện thoại", sdt) { sdt = it }
        ProfileTextField("Website", website) { website = it }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val updatedDoctor: Map<String, Any?> = mapOf(
                    "hoTen" to hoTen,
//                    "chuyenKhoa" to chuyenKhoa,
                    "chuyenKhoa" to (chuyenKhoa?.name ?: ""),
                    "diaChi" to diaChi,
                    "tieuSu" to tieuSu,
                    "kinhNghiem" to (kinhNghiem.trim().toIntOrNull() ?: 0),
                    "danhGia" to (danhGia.trim().toDoubleOrNull() ?: 0.0),
                    "benhNhanDaKham" to (benhNhanDaKham.trim().toIntOrNull() ?: 0),
                    "sdt" to sdt,
                    "anh" to anh,
                    "website" to website
                )

                db.collection("doctors").document(doctorId)
                    .update(updatedDoctor)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu thay đổi")
        }


    }
}
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = singleLine
    )
}
