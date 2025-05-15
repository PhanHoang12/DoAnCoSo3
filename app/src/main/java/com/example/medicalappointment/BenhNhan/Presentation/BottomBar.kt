package com.example.medicalapp.BenhNhan.Presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medicalappointment.BenhNhan.Data.Model.BottomNavItem

@Composable
fun BottomNavigationBar(navController: NavHostController?) {
    val items = listOf(
        BottomNavItem("Trang chủ", Icons.Default.Home, "home"),
        BottomNavItem("Lịch khám", Icons.Default.DateRange, "scheduled/{IdBenhNhan}"),
        BottomNavItem("Tin nhắn", Icons.Default.Chat, "chat_screen"),
        BottomNavItem("Tài khoản", Icons.Default.Person, "patient_profilescreen")
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 12.sp) },
                selected = false, // có thể thêm logic để set selected đúng
                onClick = {
                    navController?.navigate(item.route)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    MaterialTheme {
        BottomNavigationBar(navController = NavHostController(LocalContext.current))
    }
}