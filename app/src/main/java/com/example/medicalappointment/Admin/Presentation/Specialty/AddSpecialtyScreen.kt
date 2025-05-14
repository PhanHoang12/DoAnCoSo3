package com.example.medicalapp.Admin.Presentation.Specialty

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.medicalapp.Admin.Data.Model.Specialty

import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository
import com.example.medicalappointment.Admin.Presentation.Home.AdminTopBar
import com.example.medicalappointment.Admin.Presentation.Home.BottomNavigationBar

import com.example.medicalappointment.Admin.Presentation.Specialty.SpecialtyViewModelFactory
import com.google.firebase.storage.FirebaseStorage

@Composable
fun AddSpecialtyScreen(
    navController: NavController,
    viewModel: SpecialtyViewModel = viewModel(
        factory = SpecialtyViewModelFactory(SpecialtyRepository())
    )) {
    val context = LocalContext.current
    val message by viewModel.message.collectAsState()
    val scope = rememberCoroutineScope()

    var specialtyId by remember { mutableStateOf("") }
    var tenChuyenKhoa by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

//    LaunchedEffect (Unit){
//        viewModel.fetchSpecialty()
//    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            AdminTopBar(userName = "Admin") {
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Thêm chuyên khoa", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = specialtyId,
                onValueChange = { specialtyId = it },
                label = { Text("Mã chuyên khoa") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tenChuyenKhoa,
                onValueChange = { tenChuyenKhoa = it },
                label = { Text("Tên chuyên khoa") },
                modifier = Modifier.fillMaxWidth()
            )

            if (selectedImageUri == null) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {}, // Không cho thay đổi giá trị
                    label = { Text("Ảnh chuyên khoa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { pickImageLauncher.launch("image/*") }, // Bấm vào cả ô để chọn ảnh
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { pickImageLauncher.launch("image/*") }
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
                        model = selectedImageUri,
                        contentDescription = "Ảnh chuyên khoa",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                    IconButton(
                        onClick = { selectedImageUri = null }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa ảnh")
                    }
                }
            }

            Button(
                onClick = {
                    if (specialtyId.isBlank() || tenChuyenKhoa.isBlank()) {
                        Toast.makeText(
                            context,
                            "Vui lòng điền đầy đủ thông tin!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val specialty = Specialty(
                        id = specialtyId,
                        name = tenChuyenKhoa,
                        imageUrl = ""
                    )

                    selectedImageUri?.let { uri ->
                        val storageRef =
                            FirebaseStorage.getInstance().reference.child("specialty_images/$specialtyId")
                        val uploadTask = storageRef.putFile(uri)

                        uploadTask.addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                val updated = specialty.copy(imageUrl = downloadUrl.toString())
                                viewModel.saveSpecialty(updated)
                                navController.popBackStack()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Upload ảnh thất bại: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: run {
                        viewModel.saveSpecialty(specialty)

                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu chuyên khoa")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSpecialtyScreen() {
    val navController = rememberNavController()
    AddSpecialtyScreen(navController = navController)
}