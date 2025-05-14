package com.example.medicalappointment.Admin.Auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val role: String = "user"
)

@Composable
fun AdminUserListScreen(navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var users by remember { mutableStateOf(listOf<AppUser>()) }
    var loading by remember { mutableStateOf(true) }

    // Load users
    LaunchedEffect(Unit) {
        val snapshot = firestore.collection("users").get().await()
        users = snapshot.documents.mapNotNull {
            val uid = it.id
            val email = it.getString("email") ?: return@mapNotNull null
            val role = it.getString("role") ?: "user"
            AppUser(uid, email, role)
        }
        loading = false
    }

    Column(modifier = Modifier.padding(16.dp)) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(top = 16.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color(0xFF1976D2)
            )
        }
        Spacer(Modifier.height(10.dp))

        Text("Danh sách người dùng", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(users) { user ->
                    var expanded by remember { mutableStateOf(false) }
                    var selectedRole by remember { mutableStateOf(user.role) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Email: ${user.email}")
                            Text("Vai trò hiện tại: ${user.role}")

                            Spacer(Modifier.height(8.dp))

                            Box {
                                Button(onClick = { expanded = true }) {
                                    Text("Phân quyền")
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    listOf("user", "doctor", "admin").forEach { roleOption ->
                                        DropdownMenuItem(
                                            text = { Text(roleOption) },
                                            onClick = {
                                                expanded = false
                                                if (user.role != roleOption) {
                                                    firestore.collection("users")
                                                        .document(user.uid)
                                                        .update("role", roleOption)
                                                        .addOnSuccessListener {
                                                            selectedRole = roleOption
                                                            Toast.makeText(
                                                                context,
                                                                "Đã phân quyền thành $roleOption!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            // Reload data
                                                            firestore.collection("users").get()
                                                                .addOnSuccessListener { snapshot ->
                                                                    users =
                                                                        snapshot.documents.mapNotNull {
                                                                            val uid = it.id
                                                                            val email =
                                                                                it.getString("email")
                                                                                    ?: return@mapNotNull null
                                                                            val role =
                                                                                it.getString("role")
                                                                                    ?: "user"
                                                                            AppUser(
                                                                                uid,
                                                                                email,
                                                                                role
                                                                            )
                                                                        }
                                                                }
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(
                                                                context,
                                                                "Lỗi: ${e.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
