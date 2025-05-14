package com.example.medicalapp.BenhNhan.Presentation.Patient

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.medicalapp.BenhNhan.Repository.PatientRepository
import com.example.medicalappointment.BenhNhan.Data.Model.Patient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalProfileScreen(navController: NavHostController?) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val repository = remember { PatientRepository() }
    val coroutineScope = rememberCoroutineScope()

    var sdt by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var dob by remember { mutableStateOf("") }
    var tieusu by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf("Nam") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val datePickerState = rememberDatePickerState()
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Khởi tạo hồ sơ y tế", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = {},
            label = { Text("Ngày sinh") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Chọn ngày")
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dob = sdf.format(Date(millis))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Nam", "Nữ").forEach { sex ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { gender = sex },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = gender == sex, onClick = { gender = sex })
                    Text(sex, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = TextFieldValue(user?.email ?: ""),
            onValueChange = {},
            label = { Text("Email") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = sdt,
            onValueChange = { sdt = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tieusu,
            onValueChange = { tieusu = it },
            label = { Text("Tiểu sử bệnh") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Chọn ảnh đại diện")
        }

        selectedImageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Ảnh đại diện",
                modifier = Modifier
                    .size(100.dp)
                    .padding(top = 8.dp)
            )
        } ?: Text("Chưa chọn ảnh", modifier = Modifier.padding(top = 8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    if (name.text.isBlank() || dob.isBlank() || sdt.text.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val date = kotlin.runCatching { sdf.parse(dob) ?: Date() }.getOrDefault(Date())
                    val patient = Patient(
                        userId = user?.uid.orEmpty(),
                        hoTen = name.text,
                        gioitinh = (gender == "Nam"),
                        ngaysinh = date,
                        tieusu = tieusu.text,
                        sdt = sdt.text,
                        avatarUrl = ""
                    )

                    val result = repository.createOrUpdatePatient(
                        hoTen = patient.hoTen,
                        gioitinh = patient.gioitinh,
                        ngaysinh = patient.ngaysinh,
                        tieusu = patient.tieusu,
                        sdt = patient.sdt,
                        avatarUri = selectedImageUri
                    )

                    if (result.isSuccess) {
                        Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                        navController?.navigate("homescreen")
                    } else {
                        Toast.makeText(context, "Lưu thất bại!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Tiếp tục", fontSize = 18.sp)
        }
    }
}
