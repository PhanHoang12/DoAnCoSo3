package com.example.medicalappointment.BenhNhan.Data.Model

import java.util.Date

data class Patient(
    val userId: String = "",
    val hoTen: String = "",
    val gioitinh: Boolean = true,
    val ngaysinh: Date = Date(),
    val tieusu: String = "",
    val sdt: String = "",
    val avatarUrl: String = "",
    val email: String = ""
)