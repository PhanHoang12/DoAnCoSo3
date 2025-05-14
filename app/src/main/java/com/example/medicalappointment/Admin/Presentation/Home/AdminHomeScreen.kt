package com.example.medicalappointment.Admin.Presentation.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medicalapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun AdminHomeScreen(
    navController: NavController,
    userName: String = "Admin"
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current;

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
                DrawerItem("Phân quyền") { navController.navigate(("decentralization")) }
                DrawerItem("Quản lý lịch hẹn") { /* navController.navigate("appointment_screen") */ }
                DrawerItem("Đăng xuất") {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate("signin"){
                        popUpTo("admin_home"){inclusive = true}
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                AdminTopBar(userName = userName) {
                    scope.launch { drawerState.open() }  // Mở Drawer khi nhấn menu
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            AdminDashboardContent(modifier = Modifier.padding(paddingValues))
        }
    }
}


@Composable
fun AdminTopBar(userName: String, onMenuClick: () -> Unit) {
    Surface(
        color = Color(0xFF009DFE),
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .padding(top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.start1),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Xin chào, $userName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Row {
                IconButton(onClick = { /* TODO: Search */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }
                IconButton(onClick = onMenuClick) {  // Chuyển logic menu vào đây
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
            }
        }
    }
}


@Composable
fun DrawerItem(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}

@Composable
fun AdminDashboardContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            InfoCard(title = "Doanh thu", value = "10M", modifier = Modifier.weight(1f))
            InfoCard(title = "Người dùng", value = "100", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            InfoCard(title = "BN đã khám", value = "120", modifier = Modifier.weight(1f))
            InfoCard(title = "Feedback", value = "30", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFF009DFE)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home_hospital") },
            icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Bệnh viện", tint = Color.White) },
            label = { Text("Bệnh viện", color = Color.White) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home_patient")},
            icon = { Icon(Icons.Default.People, contentDescription = "Bệnh nhân", tint = Color.White) },
            label = { Text("Bệnh nhân", color = Color.White) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home_specialty") },
            icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Chuyên khoa", tint = Color.White) },
            label = { Text("Chuyên khoa", color = Color.White) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home_doctor") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Bác sĩ", tint = Color.White) },
            label = { Text("Bác sĩ", color = Color.White) }
        )
    }
}

