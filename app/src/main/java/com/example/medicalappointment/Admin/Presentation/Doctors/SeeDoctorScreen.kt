package com.example.medicalappointment.Admin.Presentation.Doctors

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository
import com.example.medicalapp.Admin.Presentation.Doctors.DoctorViewModel
import com.example.medicalapp.Admin.Presentation.Doctors.DoctorViewModelFactory
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar
import com.example.medicalappointment.Admin.Presentation.Home.DrawerItem
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SeeDoctorScreen(
    navController: NavController,
    doctorId: String,
    viewModel: DoctorViewModel = viewModel(factory = DoctorViewModelFactory(DoctorRepository()))
) {
    val doctor by viewModel.selectedDoctor.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Lấy thông tin bác sĩ khi doctorId thay đổi
    LaunchedEffect(doctorId) {
        viewModel.getDoctorById(doctorId)
    }

    val PrimaryBlue = Color(0xFF009DFE)
    val HeaderBackground = Color(0xFFE0F4FF)

    // ModalNavigationDrawer sẽ nằm bên ngoài AdminTopBar
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                androidx.compose.material3.Text("Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                Divider()
                DrawerItem("Bệnh viện") { navController.navigate("home_hospital") }
                DrawerItem("Bác sĩ") { navController.navigate("home_doctor") }
                DrawerItem("Bệnh nhân") { navController.navigate("home_patient") }
                DrawerItem("Chuyên khoa") { navController.navigate("home_specialty") }
                DrawerItem("Phân quyền") { navController.navigate(("decentralization")) }
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
                TopAppBar(
                    title = { Text("Thông tin bác sĩ", color = Color.White, modifier = Modifier.padding(top = 20.dp)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White,  modifier = Modifier.padding(top = 20.dp))
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

            // Nếu có thông tin bác sĩ
            doctor?.let { doc ->

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    // Hiển thị ảnh bác sĩ
                    AsyncImage(
                        model = doc.anh,
                        contentDescription = "Ảnh bác sĩ",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    // Tiêu đề đậm cho các phần thông tin


                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            Text("Họ và tên: ${doc.hoTen}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        // Chuyên khoa
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Chuyên khoa:", fontWeight = FontWeight.Bold)
                            Text("${doc.chuyenKhoa}")
                        }

                        Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa các thông tin

                        // Nơi công tác
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Nơi công tác:", fontWeight = FontWeight.Bold)
                            Text("${doc.diaChi}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Kinh nghiệm
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Kinh nghiệm:", fontWeight = FontWeight.Bold)
                            Text("${doc.kinhNghiem}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tiểu sử
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Tiểu sử:", fontWeight = FontWeight.Bold)
                            Text("${doc.tieuSu}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Số điện thoại
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Số điện thoại:", fontWeight = FontWeight.Bold)
                            Text("${doc.sdt}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Website
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Website:", fontWeight = FontWeight.Bold)
                            Text("${doc.website}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bệnh nhân đã khám
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Bệnh nhân đã khám:", fontWeight = FontWeight.Bold)
                            Text("${doc.benhNhanDaKham}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Đánh giá
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Đánh giá:", fontWeight = FontWeight.Bold)
                            Text("${doc.danhGia}")
                        }
                    }
                }

            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Hiển thị CircularProgressIndicator nếu chưa có dữ liệu bác sĩ
                CircularProgressIndicator()
            }
        }
    }
}
