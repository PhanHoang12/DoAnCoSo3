package com.example.medicalapp.Admin.Data.Repository

import android.net.Uri
import android.util.Log
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class DoctorRepository {
    private val db = FirebaseFirestore.getInstance()
    private val doctorCollection = db.collection("doctors")
    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference


    suspend fun calculateAverageRating(doctorId: String): Double{
        val snapshot = FirebaseFirestore.getInstance()
            .collection("ratedoctor")
            .whereEqualTo("doctorId", doctorId)
            .get()
            .await()

        val ratings = snapshot.documents.mapNotNull {
            (it.get("rating") as? Number)?.toDouble()
        }
        return if (ratings.isNotEmpty()) ratings.average() else 0.0
    }



    suspend fun getDoctor(): List<Doctor> {
        return try {
            val snapshot = doctorCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Doctor::class.java)?.copy(doctorId = doc.id)
            }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Lỗi khi lấy danh sách bác sĩ", e)
            emptyList()
        }
    }

    suspend fun saveDoctor(doctor: Doctor) {
        val data = hashMapOf(
            "doctorId" to doctor.doctorId,
            "hoTen" to doctor.hoTen,
            "chuyenKhoa" to doctor.chuyenKhoa,
            "diaChi" to doctor.diaChi,
            "tieuSu" to doctor.tieuSu,
            "kinhNghiem" to doctor.kinhNghiem,
//            "danhGia" to doctor.danhGia,
            "benhNhanDaKham" to doctor.benhNhanDaKham,
            "sdt" to doctor.sdt,
            "website" to doctor.website,
            "anh" to doctor.anh
        )
        doctorCollection.document(doctor.doctorId).set(data).await()
    }

    suspend fun updateDoctor(doctor: Doctor) {
        val data = mapOf(
            "hoTen" to doctor.hoTen,
            "chuyenKhoa" to doctor.chuyenKhoa,
            "diaChi" to doctor.diaChi,
            "tieuSu" to doctor.tieuSu,
            "kinhNghiem" to doctor.kinhNghiem,
            "danhGia" to doctor.danhGia,
            "benhNhanDaKham" to doctor.benhNhanDaKham,
            "sdt" to doctor.sdt,
            "website" to doctor.website,
            "anh" to doctor.anh
        )
        doctorCollection.document(doctor.doctorId).update(data).await()
    }

    suspend fun deleteDoctor(doctorId: String) {
        doctorCollection.document(doctorId).delete().await()
    }



    suspend fun getDoctorById(doctorId: String): Doctor? {
        val snapshot = firestore.collection("doctors").document(doctorId).get().await()
        return snapshot.toObject(Doctor::class.java)?.copy(doctorId = snapshot.id)
    }

}
