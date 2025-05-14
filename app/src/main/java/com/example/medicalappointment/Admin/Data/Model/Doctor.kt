package com.example.medicalapp.Admin.Data.Model


data class Doctor(
    val doctorId: String = "",
    val hoTen: String = "",
    val chuyenKhoa: String = "",
    val diaChi: String = "",
    val tieuSu: String = "",
    val kinhNghiem: Int? = null,
    val danhGia: Double? = null,
    val benhNhanDaKham: Int? = null,
    val sdt: String = "",
    val anh: String = "",
    val website: String = "",
//    val noiCongTac: String = ""
)


