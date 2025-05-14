package com.example.medicalappointment.Admin.Presentation.Hospital

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medicalapp.Admin.Data.Model.Hospital
import com.example.medicalapp.Admin.Presentation.Hospitals.HospitalViewModel
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository
import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar
import com.example.medicalappointment.Admin.Presentation.Home.DrawerItem
import com.google.firebase.auth.FirebaseAuth


import kotlinx.coroutines.launch

@Composable
fun HomeHospitalScreen(
    navController: NavController,
    viewModel: HospitalViewModel = viewModel(
        factory = HospitalViewModelFactory(HospitalRepository())
    )
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val hospitalList by viewModel.hospitals.collectAsState()
    val message by viewModel.message.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.fetchHospitals()
    }

    // Top-level color
    val PrimaryBlue = Color(0xFF009DFE)
    val HeaderBackground = Color(0xFFE0F4FF) // sáng nhẹ cho bảng header

    // ModalNavigationDrawer sẽ nằm bên ngoài AdminTopBar
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
                DrawerItem("Phân quyền") {  navController.navigate("decentralization")  }
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

            // Sử dụng Box để chứa nội dung và căn chỉnh nút thêm
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 72.dp) // Dành chỗ cho nút thêm ở dưới
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Danh sách Bệnh Viện",
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
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "ID",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp),
                                //color = PrimaryBlue
                            )
                            Text(
                                "Ảnh",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .padding(horizontal = 0.dp),
                                //color = PrimaryBlue
                            )
                            Text(
                                "Tên",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(2f)
                                    .padding(horizontal = 0.dp),
                                //color = PrimaryBlue
                            )
                            Text(
                                "Sdt",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(2.3f)
                                    .padding(horizontal = 2.dp),
                                //color = PrimaryBlue
                            )
                            Text(
                                "Hành động",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(2f)
                                    .padding(horizontal = 4.dp),
                                //color = PrimaryBlue
                            )
                        }
                    }

                    Divider()

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(hospitalList) { hospital ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    hospital.id,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 2.dp)
                                )
                                AsyncImage(
                                    model = hospital.anh,
                                    contentDescription = "Ảnh bệnh viện",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .weight(1f),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    hospital.tenBV,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(horizontal = 4.dp)
                                )
                                Text(hospital.sdt, modifier = Modifier.weight(2f))

                                Row(modifier = Modifier.weight(2f)) {
                                    IconButton(onClick = {
                                        navController.navigate("edit_hospital/${hospital.id}")

                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Sửa",
                                            tint = PrimaryBlue
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.deleteHospital(hospital)
                                        Toast.makeText(context, "Xóa thành công 1 bệnh viện!", Toast.LENGTH_SHORT).show()
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

                // Nút Thêm Bệnh Viện
                FloatingActionButton(
                    onClick = { navController.navigate("add_hospital") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = PrimaryBlue
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm Bệnh Viện",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

