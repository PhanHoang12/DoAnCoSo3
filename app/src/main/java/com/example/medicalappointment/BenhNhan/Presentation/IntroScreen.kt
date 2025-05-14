package com.example.medicalapp.BenhNhan.Presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicalapp.R
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(navController: NavHostController?) {
    val imageList = listOf(
        R.drawable.start1,
        R.drawable.start2,
        R.drawable.start3
    )
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % imageList.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageList[currentIndex]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                imageList.forEachIndexed { index, _ ->
                    val color = if (index == currentIndex) Color.White else Color.Gray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Get Started Button
            Button(
                onClick = {
                    navController?.navigate("home") // Thay "home" bằng route thực tế
                },
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Get Started")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    IntroScreen(navController = null) // Preview không cần NavController
}
@Composable
fun MainApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "intro") {
        composable("intro") {
            IntroScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
    }
}
