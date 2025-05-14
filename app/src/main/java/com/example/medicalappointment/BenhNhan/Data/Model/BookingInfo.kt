package com.example.medicalappointment.BenhNhan.Data.Model


import java.time.LocalDate

data class BookingInfo(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: String = "09:00",
    val selectedPeriod: String = "morning",
    val trangThai: String = "Chưa xác nhận",
)