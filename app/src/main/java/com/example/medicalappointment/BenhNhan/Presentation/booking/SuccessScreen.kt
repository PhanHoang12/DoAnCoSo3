package com.example.medicalappointment.BenhNhan.Presentation.booking


import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medicalapp.R

@Composable
fun SuccessScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        StepIndicator(currentStep = 3)

        Spacer(modifier = Modifier.height(16.dp))

        // Thêm Spacer để đẩy "Đặt lịch thành công!" xuống dưới một khoảng
        Spacer(modifier = Modifier.height(32.dp)) // Khoảng cách thêm vào

        Text(
            text = "Đặt lịch thành công!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Thông tin lịch hẹn đã được gửi. Vui lòng thanh toán để xác nhận.",
            fontSize = 16.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.images),
            contentDescription = "QR Code thanh toán",
            modifier = Modifier
                .size(200.dp)
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.popBackStack("homescreen", inclusive = false)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Quay về trang chủ")
        }
    }
}


@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf("Chọn lịch khám", "Xác nhận", "Nhận lịch hẹn")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                val isActive = currentStep == index + 1
                val bgColor = if (isActive) Color(0xFF1976D2) else Color.LightGray
                val textColor = if (isActive) Color.White else Color.DarkGray

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(bgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng cách giữa bước và tên bước

                Text(
                    text = step,
                    color = if (isActive) Color(0xFF1976D2) else Color.DarkGray,
                    fontSize = 14.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
            if (index < steps.size - 1) {
                Spacer(modifier = Modifier.width(16.dp)) // Khoảng cách giữa các bước
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSuccessScreen() {
    SuccessScreen(navController = rememberNavController())
}
