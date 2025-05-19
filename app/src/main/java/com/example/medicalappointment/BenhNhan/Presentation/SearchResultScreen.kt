package com.example.medicalappointment.benhnhan.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.example.medicalapp.Admin.Data.Model.Hospital
import com.example.medicalapp.Admin.Data.Model.Specialty
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository
import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository
import com.example.medicalapp.BenhNhan.Presentation.DoctorCard
import com.example.medicalapp.BenhNhan.Presentation.HospitalCard
import com.example.medicalapp.BenhNhan.Presentation.SpecialtyItem
import java.text.Normalizer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable


fun String.normalizeVietnamese(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Regex("\\p{InCombiningDiacriticalMarks}+").replace(temp, "")
        .lowercase()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    navController: NavHostController,
    query: String
) {
    val doctorRepo = remember { DoctorRepository() }
    val hospitalRepo = remember { HospitalRepository() }
    val specialtyRepo = remember { SpecialtyRepository() }

    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var hospitals by remember { mutableStateOf<List<Hospital>>(emptyList()) }
    var specialties by remember { mutableStateOf<List<Specialty>>(emptyList()) }

    LaunchedEffect(query) {
        val normalizedQuery = query.normalizeVietnamese()
        doctors = doctorRepo.getDoctor().filter {
            it.hoTen.normalizeVietnamese().contains(normalizedQuery)
        }
        hospitals = hospitalRepo.getAllHospitals().filter {
            it.tenBV.normalizeVietnamese().contains(normalizedQuery)
        }
        specialties = specialtyRepo.getSpecialty().filter {
            it.name.normalizeVietnamese().contains(normalizedQuery)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Kết quả tìm kiếm") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Text("Kết quả cho: \"$query\"", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (doctors.isNotEmpty()) {
                item {
                    Text("Bác sĩ", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(doctors) { doctor ->
                    DoctorCard(doctor, onClick = {
                        navController.navigate("doctor_detail/${doctor.doctorId}")
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (hospitals.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Bệnh viện", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(hospitals) { hospital ->
                    HospitalCard(hospital, onClick = {
                        navController.navigate("hospital_detail/${hospital.id}")
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (specialties.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chuyên khoa", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(specialties) { specialty ->
                    SpecialtyItem(specialty, onClick = {
                        navController.navigate("doctors_by_specialty/${specialty.name}")
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (doctors.isEmpty() && hospitals.isEmpty() && specialties.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Không tìm thấy kết quả nào", style = MaterialTheme.typography.bodyLarge)
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp)) // thêm không gian cuối
            }
        }
    }
}
