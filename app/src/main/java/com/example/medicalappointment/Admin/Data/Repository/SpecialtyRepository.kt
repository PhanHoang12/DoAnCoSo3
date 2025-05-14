package com.example.medicalappointment.Admin.Data.Repository

import android.net.Uri
import android.util.Log
import com.example.medicalapp.Admin.Data.Model.Specialty
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class SpecialtyRepository {
    private val db = FirebaseFirestore.getInstance();
    private val specialtyCollection = db.collection("specialtys")
    private val storageRef = FirebaseStorage.getInstance().reference

    suspend fun getSpecialty(): List<Specialty>{
        return try {
            val snapshot = specialtyCollection.get().await()
            snapshot.documents.mapNotNull { spe ->
                spe.toObject(Specialty::class.java)?.copy(id = spe.id)
            }
        }catch (e: Exception){
            Log.e("FirestoreError", "Lỗi khi lấy danh sách chuyên khoa", e)
            emptyList()
        }
    }

    suspend fun addSpecialty(specialty: Specialty, imageUri: Uri? = null): Boolean{
        return try {
            if (specialty.id.isEmpty()){
                Log.e("Firestore", "❌ SpecialtyId không được để trống")
                return false
            }

            val document = specialtyCollection.document(specialty.id).get().await()
            if (document.exists()){
                Log.e("Firestore", "❌ SpecialtyId ${specialty.id} đã tồn tại")
                return false
            }
            val imageUrl = imageUri?.let { uploadImageToFirebase(it, specialty.id) } ?:""

            val firestoreSpecialty = specialty.copy(imageUrl = imageUrl)
            specialtyCollection.document(specialty.id).set(firestoreSpecialty).await()

            Log.d("Firestore", "✅ Specialty ${specialty.name} đã được thêm với ảnh $imageUrl")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Lỗi khi thêm bác sĩ: ${e.message}", e)
            false
        }
    }

    suspend fun updateSpecialty(specialtyId: String, imageUri: Uri? = null): Boolean{
        return try {
            // Kiểm tra bác sĩ có tồn tại hay không
            val document = specialtyCollection.document(specialtyId).get().await()
            if (!document.exists()) {
                Log.e("Firestore", "❌ Không tìm thấy chuyên khoa với specialtyId: $specialtyId")
                return false
            }

            val imageUrl = imageUri?.let { uploadImageToFirebase(it, specialtyId) } ?: return false
            // Cập nhật ảnh vào Firestore
            specialtyCollection.document(specialtyId).update("anh", imageUrl).await()

            Log.d("Firestore", "✅ Ảnh của bác sĩ $specialtyId đã được cập nhật")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Lỗi khi cập nhật ảnh chuyên khoa: ${e.message}", e)
            false
        }
    }

    private suspend fun uploadImageToFirebase(imageUri: Uri, specialtyId: String): String? {
        return try {
            val imageRef = storageRef.child("specialty_images/$specialtyId.jpg")
            imageRef.putFile(imageUri).await()

            // Lấy URL ảnh sau khi tải lên thành công
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Log.d("FirebaseStorage", "✅ Ảnh tải lên thành công: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "❌ Lỗi khi tải ảnh lên Firebase Storage", e)
            null
        }
    }
}