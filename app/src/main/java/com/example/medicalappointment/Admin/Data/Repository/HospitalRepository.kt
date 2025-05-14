package com.example.medicalappointment.Admin.Data.Repository

import android.net.Uri
import android.util.Log
import com.example.medicalapp.Admin.Data.Model.Hospital
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class HospitalRepository() {

    private val db = FirebaseFirestore.getInstance()
    private val hospitalCollection = db.collection("hospitals")
    private val storageReference = FirebaseStorage.getInstance().reference

    suspend fun getAllHospitals(): List<Hospital> {
        return try {
            val snapshot = hospitalCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Hospital::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Lỗi khi lấy danh sách bệnh viện", e)
            emptyList()
        }
    }

    suspend fun uploadImage(hospitalId: String, imageUri: Uri): String? {
        return try {
            val imageRef = storageReference.child("hospital_images/$hospitalId.jpg")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("Storage", "❌ Upload ảnh thất bại: ${e.message}")
            null
        }
    }

    suspend fun addHospital(hospital: Hospital): Boolean {
        return try {
            if (hospital.id.isBlank()) {
                Log.e("Firestore", "❌ Hospital Id không được để trống")
                return false
            }

            val exists = hospitalCollection.document(hospital.id).get().await().exists()
            if (exists) {
                Log.e("Firestore", "❌ Hospital Id ${hospital.id} đã tồn tại")
                return false
            }

            hospitalCollection.document(hospital.id).set(hospital).await()
            Log.d("Firestore", "✅ Thêm bệnh viện thành công: ${hospital.tenBV}")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Lỗi khi thêm bệnh viện", e)
            false
        }
    }

    // 📌 Cập nhật bệnh viện
    suspend fun updateHospital(hospital: Hospital): Boolean {
        return try {
            if (hospital.id.isBlank()) {
                Log.e("Firestore", "❌ Hospital Id không được để trống")
                return false
            }

            hospitalCollection.document(hospital.id).set(hospital).await()
            Log.d("Firestore", "✅ Cập nhật bệnh viện thành công: ${hospital.tenBV}")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Lỗi khi cập nhật bệnh viện", e)
            false
        }
    }


    // 📌 Xóa bệnh viện
    suspend fun deleteHospital(hospitalId: String): Boolean {
        return try {
            hospitalCollection.document(hospitalId).delete().await()
            Log.d("Firestore", "✅ Đã xóa bệnh viện với Id: $hospitalId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Lỗi khi xóa bệnh viện", e)
            false
        }
    }

    suspend fun getHospitalById(id: String): Hospital? {
        return try {
            val doc = db.collection("hospitals").document(id).get().await()
            if (doc.exists()) doc.toObject(Hospital::class.java)?.copy(id = doc.id) else null
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Lỗi khi lấy chi tiết bệnh viện", e)
            null
        }
    }
}
