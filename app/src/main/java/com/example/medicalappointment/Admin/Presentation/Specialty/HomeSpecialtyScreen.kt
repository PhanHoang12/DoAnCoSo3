package com.example.medicalappointment.Admin.Presentation.Specialty

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.medicalapp.Admin.Presentation.Specialty.SpecialtyViewModel
import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository
import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar
import com.example.medicalappointment.Admin.Presentation.Home.DrawerItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun HomeSpecialtyScreen(
    navController: NavController,
    viewModel: SpecialtyViewModel = viewModel(
        factory = SpecialtyViewModelFactory(SpecialtyRepository())
    )
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val specialtyList by viewModel.specialtys.collectAsState()
    val message by viewModel.message.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Hiển thị thông báo khi có message mới
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.fetchSpecialtys()
    }

    // Màu sắc cho các thành phần
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
                    // Tiêu đề
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Danh sách chuyên khoa",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    Row(
                        modifier = Modifier
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

                    // Hiển thị danh sách chuyên khoa
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(specialtyList) { specialty ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    specialty.id,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 2.dp)
                                )
                                AsyncImage(
                                    model = specialty.imageUrl,
                                    contentDescription = "Ảnh chuyên khoa",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .weight(1f),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    specialty.name,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(horizontal = 4.dp)
                                )

                                Row(modifier = Modifier.weight(2f)) {
                                    IconButton(onClick = {
                                        // Sửa (cần cài đặt hành động sửa)
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Sửa",
                                            tint = PrimaryBlue
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.deleteSpecialty(specialty)
                                        Toast.makeText(context, "Xóa thành công 1 chuyên khoa!", Toast.LENGTH_SHORT).show()
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

                FloatingActionButton(
                    onClick = { navController.navigate("add_specialty") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = PrimaryBlue
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm chuyên khoa",
                        tint = Color.White
                    )
                }
            }
        }
    }
}