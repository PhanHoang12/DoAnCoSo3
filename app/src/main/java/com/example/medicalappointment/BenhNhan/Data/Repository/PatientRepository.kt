package com.example.medicalapp.BenhNhan.Repository

import android.net.Uri
import android.util.Log
import com.example.medicalappointment.BenhNhan.Data.Model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date

class PatientRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun createOrUpdatePatient(
        hoTen: String,
        gioitinh: Boolean,
        ngaysinh: Date,
        tieusu: String,
        sdt: String,
        avatarUri: Uri?
    ): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val email = auth.currentUser?.email ?: ""

            val existingPatient = getPatientInfo().getOrNull()
            val currentAvatarUrl = existingPatient?.avatarUrl ?: ""  // Get the existing avatar URL

            val avatarUrl = if (avatarUri != null) {
                val storageRef = storage.reference.child("avatars/$uid.jpg")
                storageRef.putFile(avatarUri).await()
                storageRef.downloadUrl.await().toString()
            } else {
                currentAvatarUrl
            }

            val patient = Patient(
                userId = uid,
                hoTen = hoTen,
                gioitinh = gioitinh,
                ngaysinh = ngaysinh,
                tieusu = tieusu,
                sdt = sdt,
                avatarUrl = avatarUrl,
                email = email
            )

            db.collection("patients").document(uid).set(patient).await()
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("PatientRepository", "Error saving patient info: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getPatientInfo(): Result<Patient?> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val snapshot = db.collection("patients").document(uid).get(Source.SERVER).await()
            val patient = snapshot.toObject(Patient::class.java)
            Result.success(patient)
        } catch (e: Exception) {
            Log.e("PatientRepository", "Error fetching patient info: ${e.message}", e)
            Result.failure(e)
        }
    }



    suspend fun updatePatientInfo(patient: Patient): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val patientToUpdate = patient.copy(userId = uid)

            db.collection("patients").document(uid).set(patientToUpdate).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PatientRepository", "Error updating patient info: ${e.message}", e)
            Result.failure(e)
        }
    }
}
