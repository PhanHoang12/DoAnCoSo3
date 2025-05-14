package com.example.medicalapp.BenhNhan.Presentation.Patient

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.navigation.NavHostController
import com.example.medicalappointment.BenhNhan.Presentation.Patient.PatientViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    navController: NavHostController,
    viewModel: PatientViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Nam", "Nữ")
    val context = LocalContext.current
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    val patient by viewModel.patient.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var editing by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    patient?.let { p ->
        var avatarUrl by remember { mutableStateOf(p.avatarUrl) }
        var hoTen by remember { mutableStateOf(TextFieldValue(p.hoTen)) }
        var gioiTinh by remember { mutableStateOf(if (p.gioitinh) "Nam" else "Nữ") }
        var ngaySinh by remember {
            mutableStateOf(
                TextFieldValue(
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(p.ngaysinh)
                )
            )
        }
        var sdt by remember { mutableStateOf(TextFieldValue(p.sdt)) }
        var tieusu by remember { mutableStateOf(TextFieldValue(p.tieusu)) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("← Quay lại", style = MaterialTheme.typography.labelLarge)
                }
                TextButton(onClick = { editing = !editing }) {
                    Text(if (editing) "Huỷ" else "Chỉnh sửa", style = MaterialTheme.typography.labelLarge)
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = selectedImageUri ?: avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable(enabled = editing) {
                        imagePickerLauncher.launch("image/*")
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = hoTen,
                        onValueChange = { hoTen = it },
                        label = { Text("Họ tên") },
                        enabled = editing,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = gioiTinh,
                            onValueChange = {},
                            label = { Text("Giới tính") },
                            enabled = editing,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            genderOptions.forEach { gender ->
                                DropdownMenuItem(
                                    text = { Text(gender) },
                                    onClick = {
                                        gioiTinh = gender
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ngaySinh,
                        onValueChange = { ngaySinh = it },
                        label = { Text("Ngày sinh (dd/MM/yyyy)") },
                        enabled = editing,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sdt,
                        onValueChange = { sdt = it },
                        label = { Text("Số điện thoại") },
                        enabled = editing,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tieusu,
                        onValueChange = { tieusu = it },
                        label = { Text("Tiểu sử") },
                        enabled = editing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    if (editing) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ElevatedButton(
                            onClick = {
                                viewModel.updatePatientInfo(
                                    hoTen = hoTen.text,
                                    sdt = sdt.text,
                                    tieusu = tieusu.text,
                                    gioitinh = gioiTinh == "Nam",
                                    ngaysinhStr = ngaySinh.text
                                )
                                selectedImageUri?.let { uri ->
                                    viewModel.uploadAvatar(uri)
                                }
                                editing = false
                            },
                            enabled = !loading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (loading) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            else Text("Lưu thay đổi")
                        }
                    }
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Lỗi: $it", color = MaterialTheme.colorScheme.error)
            }

            LaunchedEffect(saveSuccess) {
                if (saveSuccess) {
                    Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                    viewModel.resetSaveSuccess()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ElevatedButton(
                onClick = {
                    viewModel.logout()
                    Toast.makeText(context, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate("signin") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Đăng xuất", color = Color.White)
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
