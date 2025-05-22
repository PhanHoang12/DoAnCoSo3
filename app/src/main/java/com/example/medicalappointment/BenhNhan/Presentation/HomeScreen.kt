package com.example.medicalapp.BenhNhan.Presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.example.medicalapp.Admin.Data.Model.Hospital
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository
import com.example.medicalapp.BenhNhan.Repository.PatientRepository
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository
import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository
import com.example.medicalapp.Admin.Data.Model.Specialty
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController?) {
    var patientName by remember { mutableStateOf("Người dùng") }
    val repository = remember { PatientRepository() }

    val doctorRepository = remember { DoctorRepository() }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }

    val hospitalRepository = remember { HospitalRepository() }
    var hospitals by remember { mutableStateOf<List<Hospital>>(emptyList()) }

    val specialtyRepository = remember { SpecialtyRepository() }
    var specialties by remember { mutableStateOf<List<Specialty>>(emptyList()) }
    val chunkedSpecialties = chunkSpecialties(specialties, 8)

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val filteredDoctors = remember(searchQuery) {
        doctors.filter { it.hoTen.contains(searchQuery.text, ignoreCase = true) }
    }

    val filteredHospitals = remember(searchQuery) {
        hospitals.filter { it.tenBV.contains(searchQuery.text, ignoreCase = true) }
    }

    val filteredSpecialities = remember(searchQuery) {
        specialties.filter { it.name.contains(searchQuery.text, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        doctors = doctorRepository.getDoctor()
        hospitals = hospitalRepository.getAllHospitals()
        specialties = specialtyRepository.getSpecialty()
    }

    LaunchedEffect(Unit) {
        repository.getPatientInfo().onSuccess { patient ->
            patient?.let { patientName = it.hoTen }
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2196F3))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable {
                                navController?.navigate("patient_profilescreen")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val firstChar = patientName.firstOrNull()?.uppercaseChar() ?: 'U'
                        Text(text=firstChar.toString(), color= Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Xin chào, $patientName",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Thông báo",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            // Điều hướng tới NotificationScreen với ID của bệnh nhân
                            navController?.navigate("notification_screen/${Firebase.auth.currentUser?.uid}")
                        }
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tên bác sĩ, bệnh viện, chuyên khoa...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(50.dp)),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier
                            .clickable {
                                navController?.navigate("search_result/${searchQuery.text}")
                            })
                    },
                    shape = RoundedCornerShape(50.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 0.sp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions (
                        onSearch = {
                            navController?.navigate("search_result/${searchQuery.text}")
                        }
                    )
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Bác sĩ", style = MaterialTheme.typography.titleMedium)

            LaunchedEffect(Unit) {
                doctors = doctorRepository.getDoctor()
            }

            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(doctors) { doctor ->
                    DoctorCard(doctor = doctor){
                        navController?.navigate("doctor_detail/${doctor.doctorId}")
                    }
                }
            }
            LaunchedEffect(Unit) {
                doctors = doctorRepository.getDoctor()
                hospitals = hospitalRepository.getAllHospitals()
            }
            Text("Bệnh viện", style = MaterialTheme.typography.titleMedium)
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(hospitals) { hospital ->
                    HospitalCard(hospital = hospital) {
                        navController?.navigate("hospital_detail/${hospital.id}")
                    }
                }
            }

            LaunchedEffect(Unit) {
                specialties = specialtyRepository.getSpecialty()
            }


            Text("Khám theo chuyên khoa", style = MaterialTheme.typography.titleMedium)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(chunkedSpecialties) { page ->
                    Column(
                        modifier = Modifier
                            .width(800.dp)
                            .padding(end = 16.dp)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            userScrollEnabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            content = {
                                items(page) { specialty ->
                                    SpecialtyItem(specialty = specialty) {
                                        navController?.navigate("doctors_by_specialty/${specialty.name}")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

fun chunkSpecialties(list: List<Specialty>, chunkSize: Int): List<List<Specialty>> {
    return list.chunked(chunkSize)
}


@Composable
fun DoctorCard(doctor: Doctor, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = doctor.anh),
            contentDescription = "Ảnh bác sĩ",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = doctor.hoTen, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun HospitalCard(hospital: Hospital, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(hospital.anh),
            contentDescription = "Ảnh bệnh viện",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = hospital.tenBV,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis

        )
    }
}


@Composable
fun SpecialtyItem(specialty: Specialty, onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE3F2FD),
            modifier = Modifier.size(64.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = specialty.imageUrl),
                contentDescription = null,
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(
            text = specialty.name,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(navController = null)
    }
}