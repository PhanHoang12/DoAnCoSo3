package com.example.medicalappointment.Admin.Presentation.Patient


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import com.example.medicalapp.Admin.Data.Repository.PatientRepository
import com.example.medicalapp.Admin.Presentation.Patient.PatientViewModel
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar


@Composable
fun SeePatientScreen(
    navController: NavController,
    patientId: String,
    viewModel: PatientViewModel = viewModel(factory = PatientViewModelFactory(PatientRepository()))
) {
    val patient by viewModel.selectedPatient.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(patientId) {
        viewModel.getPatientById(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin bệnh nhân", color = Color.White, modifier = Modifier.padding(top = 20.dp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White, modifier = Modifier.padding(top = 20.dp))
                    }
                },
                backgroundColor = Color(0xFF009DFE),
                contentColor = Color.White,
                modifier = Modifier.height(80.dp)
            )
        },
        bottomBar = { BottomNavigationBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        patient?.let { p ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray) // Bạn có thể chọn màu nền khác
                ) {
                    Text(
                        text = p.hoTen.first().toString().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                InfoRow("Họ và tên:", p.hoTen)
                InfoRow("Ngày sinh:", p.ngaysinh.toString()) // Chắc chắn phải format ngày sinh
                InfoRow("Giới tính:", if (p.gioitinh) "Nam" else "Nữ")
                InfoRow("Địa chỉ:", p.tieusu)
                InfoRow("Số điện thoại:", p.sdt)
                InfoRow("Email:", p.email)
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

