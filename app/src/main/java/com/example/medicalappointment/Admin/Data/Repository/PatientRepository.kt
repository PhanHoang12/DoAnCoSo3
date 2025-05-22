package com.example.medicalapp.Admin.Data.Repository

import android.util.Log
import com.example.medicalappointment.BenhNhan.Data.Model.Patient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PatientRepository {
    private val db = FirebaseFirestore.getInstance()
    private val patientCollection = db.collection("patients")


    suspend fun getPatientList(): List<Patient> {
        return try {
            val snapshot = patientCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Patient::class.java) }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Lỗi khi lấy danh sách bệnh nhân", e)
            emptyList()
        }
    }

    fun getPatientById(patientId: String, onResult: (Patient?) -> Unit) {
        db.collection("patients").document(patientId).get()
            .addOnSuccessListener { snapshot ->
                val patient = snapshot.toObject(Patient::class.java)
                onResult(patient)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
    suspend fun deletePatient(userId: String): Boolean {
        return try {
            patientCollection.document(userId).delete().await()
            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .delete()
                .await()
            Log.d("FirestoreSuccess", "✅ Xóa bệnh nhân $userId thành công")
            true
        } catch (e: Exception) {
            Log.e("FirestoreError", "❌ Lỗi khi xóa bệnh nhân $userId", e)
            false
        }
    }
}