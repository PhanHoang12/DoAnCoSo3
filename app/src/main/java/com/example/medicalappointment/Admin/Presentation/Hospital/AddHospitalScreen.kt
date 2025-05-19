package com.example.medicalappointment.Admin.Presentation.Hospital

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.medicalapp.Admin.Data.Model.Hospital
import com.example.medicalapp.Admin.Presentation.Hospitals.HospitalViewModel
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository

import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar
import com.example.medicalappointment.Admin.Presentation.Home.DrawerItem
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.launch

@Composable
fun AddHospitalScreen(
    navController: NavController,
    viewModel: HospitalViewModel = viewModel(
        factory = HospitalViewModelFactory(HospitalRepository())
    )
) {
    val context = LocalContext.current
    val message by viewModel.message.collectAsState()
    val scope = rememberCoroutineScope()

    var hospitalId by remember { mutableStateOf("") }
    var tenBenhVien by remember { mutableStateOf("") }
    var diaChi by remember { mutableStateOf("") }
    var sdt by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(DrawerValue.Closed)


    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchHospitals()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
        }
    }
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
                    navController.navigate("signin") {
                        popUpTo("admin_home") { inclusive = true }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                AdminTopBar(userName = "Admin") {
                    scope.launch { drawerState.open() }
                }
            },
            bottomBar = {
                BottomNavigationBar(navController)
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Thêm bệnh viện",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = hospitalId,
                            onValueChange = { hospitalId = it },
                            label = { Text("Mã bệnh viện") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tenBenhVien,
                            onValueChange = { tenBenhVien = it },
                            label = { Text("Tên bệnh viện") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = diaChi,
                            onValueChange = { diaChi = it },
                            label = { Text("Địa chỉ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = sdt,
                            onValueChange = { sdt = it },
                            label = { Text("Số điện thoại") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        //Divider(modifier = Modifier.padding(vertical = 8.dp))

                        if (imageUri == null) {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {}, // Không cho thay đổi giá trị
                                label = { Text("Ảnh bệnh viện") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { launcher.launch("image/*") }, // Bấm vào cả ô để chọn ảnh
                                readOnly = true,
                                //enabled = false, // Vô hiệu hóa nhập liệu
                                trailingIcon = {
                                    IconButton(
                                        onClick = { launcher.launch("image/*") }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = "Chọn ảnh"
                                        )
                                    }
                                }
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Ảnh bệnh viện",
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                )
                                IconButton(
                                    onClick = { imageUri = null }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Xóa ảnh"
                                    )
                                }
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (hospitalId.isBlank() || tenBenhVien.isBlank()) {
                            Toast.makeText(context, "Vui lòng điền mã và tên!", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }

                        scope.launch {
                            if (imageUri != null) {
                                val url = viewModel.uploadImage(imageUri!!, hospitalId)
                                uploadedImageUrl = url ?: ""
                            }

                            val hospital = Hospital(
                                id = hospitalId,
                                tenBV = tenBenhVien,
                                diaChi = diaChi,
                                sdt = sdt,
                                website = website,
                                anh = uploadedImageUrl
                            )
                            viewModel.saveHospital(hospital)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Lưu bệnh viện", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAddHospitalScreen() {
    val navController = rememberNavController()
    AddHospitalScreen(navController = navController)
}

