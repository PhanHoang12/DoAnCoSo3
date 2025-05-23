package com.example.medicalapp.Admin.Auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medicalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun SignInScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }


    val auth = FirebaseAuth.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }

    var hasNavigated by remember { mutableStateOf(false) }

    var currentUser by remember { mutableStateOf<FirebaseUser?>(null) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)

        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }
    // Nếu đã đăng nhập thì điều hướng theo role
    LaunchedEffect(currentUser) {
        if (currentUser != null && !hasNavigated) {
            hasNavigated = true
            val uid = currentUser!!.uid

            val testDoctorId = "HP01"
            FirebaseFirestore.getInstance()
                .collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role")?.trim()?.lowercase()
                    when (role) {
                        "admin" -> navController.navigate("admin_home") {
                            popUpTo("signin") { inclusive = true }
                            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        }

                        "doctor" -> navController.navigate("homeDoctor/${testDoctorId}") {
                            popUpTo("signin") { inclusive = true }
                            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        }

                        "user" -> {
                            val patientRef = FirebaseFirestore.getInstance()
                                .collection("patients").document(uid)
                            patientRef.get()
                                .addOnSuccessListener { patientDoc ->
                                    if (patientDoc.exists()) {
                                        val hoTen = patientDoc.getString("hoTen")
                                        if (!hoTen.isNullOrBlank()) {
                                            navController.navigate("homescreen") {
                                                popUpTo("signin") { inclusive = true }
                                                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            navController.navigate("patient_profile") {
                                                popUpTo("signin") { inclusive = true }
                                            }
                                        }
                                    } else {
                                        navController.navigate("patient_profile") {
                                            popUpTo("signin") { inclusive = true }
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }

    // Nếu chưa đăng nhập thì hiển thị giao diện đăng nhập
    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_medical),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Welcome to Medical App",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Đăng nhập", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Mật khẩu") },
                            visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff

                                val description = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = description)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = {
                                when {
                                    email.isEmpty() -> Toast.makeText(context, "Email không được để trống!", Toast.LENGTH_SHORT).show()
                                    !email.contains("@") || !email.contains(".") -> Toast.makeText(context, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                                    password.isEmpty() -> Toast.makeText(context, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show()
                                    else -> {
//                                        auth.signInWithEmailAndPassword(email, password)
//                                            .addOnCompleteListener { task ->
//                                                if (!task.isSuccessful) {
//
//                                                    Toast.makeText(context, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
                                        // Kiểm tra email có tồn tại trong bảng users không
                                        val db = FirebaseFirestore.getInstance()
                                        db.collection("users")
                                            .whereEqualTo("email", email.trim())
                                            .get()
                                            .addOnSuccessListener { documents ->
                                                if (documents.isEmpty) {
                                                    Toast.makeText(context, "Bạn chưa có tài khoản, vui lòng đăng ký.", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    auth.signInWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener { task ->
                                                            if (!task.isSuccessful) {
                                                                Toast.makeText(context, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Lỗi kết nối đến cơ sở dữ liệu!", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("Đăng nhập")
                        }

                        TextButton(onClick = { navController.navigate("signup") }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Chưa có tài khoản? Đăng ký ngay")
                        }

                        TextButton(onClick = { showResetDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Quên mật khẩu ")
                        }
                    }
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Đặt lại mật khẩu") },
                text = {
                    Column {
                        Text("Nhập Email bạn đã đăng ký trên Firebase để nhận link Reset mật khẩu!")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (resetEmail.isNotBlank()) {
                            auth.sendPasswordResetEmail(resetEmail)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Đã gửi link reset!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Không thể gửi email! Kiểm tra lại địa chỉ!", Toast.LENGTH_SHORT).show()
                                    }
                                    showResetDialog = false
                                }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập địa chỉ Email!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Gửi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(navController = rememberNavController())
}
