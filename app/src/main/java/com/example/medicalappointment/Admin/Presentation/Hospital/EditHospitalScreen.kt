package com.example.medicalappointment.Admin.Presentation.Hospital

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medicalapp.Admin.Data.Model.Hospital
import com.example.medicalapp.Admin.Presentation.Hospitals.HospitalViewModel
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHospitalScreen(
    navController: NavController,
    hospitalId: String
) {
    val repository = remember { HospitalRepository() }  // Khởi tạo repository của bạn
    val viewModel: HospitalViewModel = viewModel(
        factory = HospitalViewModelFactory(repository)
    )

    var hospitalName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val selectedHospital by viewModel.selectedHospital.collectAsState()

    LaunchedEffect(hospitalId) {
        viewModel.fetchHospitalById(hospitalId)
    }

    selectedHospital?.let { hospital ->
        hospitalName = hospital.tenBV
        address = hospital.diaChi
        phone = hospital.sdt
        website = hospital.website
        currentImageUrl = hospital.anh
    } ?: run {
        Toast.makeText(context, "Dữ liệu bệnh viện không có sẵn", Toast.LENGTH_SHORT).show()
    }
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sửa thông tin bệnh viện") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (imageUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected hospital image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else if (currentImageUrl.isNotEmpty()) {
                // Nếu không có ảnh chọn mới, hiển thị ảnh cũ
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = currentImageUrl,
                    contentDescription = "Current hospital image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            OutlinedTextField(
                value = hospitalName,
                onValueChange = { hospitalName = it },
                label = { Text("Tên bệnh viện") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Địa chỉ bệnh viện") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chọn ảnh bệnh viện
            Button(onClick = {
                pickImageLauncher.launch("image/*")
            }) {
                Text("Chọn ảnh bệnh viện")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val finalImageUri = imageUri ?: Uri.parse(currentImageUrl)

                    val updatedHospital = Hospital(
                        id = hospitalId,
                        tenBV = hospitalName,
                        diaChi = address,
                        sdt = phone,
                        website = website,
                        anh = finalImageUri.toString()
                    )

                    viewModel.updateHospital(updatedHospital)
                    Toast.makeText(context, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cập nhật thông tin")
            }
        }
    }
}

