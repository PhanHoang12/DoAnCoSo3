package com.example.medicalapp.Admin.Presentation.Patient

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicalapp.Admin.Data.Repository.PatientRepository
import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar
import com.example.medicalappointment.Admin.Presentation.Home.DrawerItem
import com.example.medicalappointment.Admin.Presentation.Patient.PatientViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun HomePatientScreen(
    navController: NavController,
    viewModel: PatientViewModel = viewModel(
        factory = PatientViewModelFactory(PatientRepository())
    )
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val patientList by viewModel.patient.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchPatient()
    }

    val PrimaryBlue = Color(0xFF009DFE)
    val HeaderBackground = Color(0xFFE0F4FF)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    navController.navigate("signin"){
                        popUpTo("admin_home"){inclusive=true}
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
            bottomBar = { BottomNavigationBar(navController) }
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 72.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Danh sách Bệnh Nhân",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    // Header
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(HeaderBackground)
                            .padding(8.dp)
                    ) {
                        Text("Tên", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text(
                            "Email",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            "SĐT",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            "Hành động",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(2f)
                        )
                    }

                    Divider()

                    LazyColumn {
                        items(patientList) { patient ->  // items(patientList) đúng
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(patient.hoTen, modifier = Modifier.weight(1f))
                                Text(patient.email, modifier = Modifier.weight(2f))
                                Text(patient.sdt, modifier = Modifier.weight(2f))

                                Row(modifier = Modifier.weight(2f)) {
                                    IconButton(onClick = {
                                        navController.navigate("see_patient/${patient.userId}")
                                    }) {
                                        Icon(
                                            Icons.Default.Visibility,
                                            contentDescription = "Xem",
                                            tint = PrimaryBlue
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.deletePatient(patient.userId)
                                        Toast.makeText(context, "Xóa thành công 1 bệnh nhân!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Xoá",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                            Divider()
                        }
                    }

                }
            }
        }
    }
}
