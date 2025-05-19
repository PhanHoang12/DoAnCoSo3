package com.example.medicalapp.Admin.Auth

import android.widget.Toast
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current;
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var showOtpDialog by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    val auth = FirebaseAuth.getInstance()

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
            Text(
                "Đăng ký tài khoản",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

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
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description =
                                if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Xác nhận mật khẩu") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description =
                                if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"

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
                            if (email.isEmpty()) {
                                Toast.makeText(context, "Email không được để trống!", Toast.LENGTH_SHORT).show()
                            } else if (!email.contains("@") || !email.contains(".")) {
                                Toast.makeText(context, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                            } else if (password.isEmpty()) {
                                Toast.makeText(context, "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show()
                            } else if (password.length < 6) {
                                Toast.makeText(context, "Mật khẩu có ít nhất 6 kí tự!", Toast.LENGTH_SHORT).show()
                            } else if (password != confirmPassword) {
                                Toast.makeText(
                                    context, "Mật khẩu và xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
                            } else {
//                                    otpSent = generateOtp()
//                                    sendOtpToEmail(email, otpSent) { success ->
//                                        if (success) {
//                                            showOtpDialog = true
//                                        } else {
//                                            Toast.makeText(context, "Gửi email thất bại!", Toast.LENGTH_SHORT).show()
//                                        }
//                                    }
                                // Check Email trong bảng users
                                val db = FirebaseFirestore.getInstance()
                                val usersRef = db.collection("users")

                                usersRef.whereEqualTo("email", email)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (!documents.isEmpty) {
                                            Toast.makeText(context, "Email đã tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            otpSent = generateOtp()
                                            sendOtpToEmail(email, otpSent) { success ->
                                                if (success) {
                                                    showOtpDialog = true
                                                } else {
                                                    Toast.makeText(context, "Gửi email thất bại!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Lỗi khi kiểm tra email: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("Đăng ký")
                    }

                    TextButton(
                        onClick = { navController.navigate("signin") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Đã có tài khoản? Đăng nhập ngay")
                    }

                    if (message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message,
                            color = if (message.contains("thành công")) Color.Green else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        if (showOtpDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(onClick = {
                        if (otpInput == otpSent) {
                            // Xác thực OTP xong, mới tạo tài khoản Firebase
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val uid = task.result?.user?.uid
                                        val db = FirebaseFirestore.getInstance()
                                        val userRef = db.collection("users")

                                        userRef.get().addOnSuccessListener { snapshot ->
                                            val isFirstUser = snapshot.isEmpty
                                            val role = if (isFirstUser) "admin" else "user"

                                            val user = hashMapOf(
                                                "email" to email,
                                                "role" to role,
                                                "userId" to uid
                                            )

                                            uid?.let {
                                                userRef.document(it).set(user)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Đăng ký thành công!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        showOtpDialog = false
                                                        navController.navigate("signin") {
                                                            popUpTo("signup") { inclusive = true }
                                                        }
                                                    }.addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Lỗi khi lưu dữ liệu người dùng",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Lỗi tạo tài khoản: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                        } else {
                            Toast.makeText(context, "Mã OTP sai, thử lại!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                        Text("Xác nhận")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOtpDialog = false }) {
                        Text("Hủy")
                    }
                },
                title = { Text("Xác thực Email") },
                text = {
                    Column {
                        Text("Vui lòng nhập mã OTP đã gửi tới email $email")
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = { Text("Mã OTP") }
                        )
                    }
                }
            )
        }
    }
}

fun generateOtp(): String {
    val chars = "0123456789"
    return (1..6).map { chars.random() }.joinToString("")
}

fun sendOtpToEmail(email: String, otp: String, callback: (Boolean) -> Unit) {
    val url = "https://formspree.io/f/xjkwerel"/*"https://formspree.io/f/xovdnwzj"*/
    val client = OkHttpClient()

    val formBody = FormBody.Builder()
        .add("email", email)
        .add("message", "Mã OTP của bạn là: $otp")
        .build()

    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(false)
        }

        override fun onResponse(call: Call, response: Response) {
            callback(response.isSuccessful)
        }
    })
}


@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(navController = rememberNavController())
}