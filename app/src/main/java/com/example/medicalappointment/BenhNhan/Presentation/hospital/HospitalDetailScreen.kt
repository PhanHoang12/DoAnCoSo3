package com.example.medicalappointment.BenhNhan.Presentation.hospital


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.medicalapp.Admin.Data.Model.Hospital

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalDetailScreen(navController: NavController?, hospital: Hospital) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết Bệnh viện") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(hospital.anh),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = hospital.tenBV, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Địa chỉ: ${hospital.diaChi}")
            Text(text = "SĐT: ${hospital.sdt}")
            Text(text = "Website: ${hospital.website}", color = Color.Blue)
        }
    }
}