package com.example.medicalapp.Admin.Presentation.Doctors

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalapp.Admin.Data.Model.Doctor
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DoctorViewModel(private val repository: DoctorRepository) : ViewModel() {
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> get() = _doctors
    init {
        fetchDoctors()
    }

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor

    init {
        fetchDoctors()
    }

    fun fetchDoctorById(doctorId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val doc = db.collection("doctors").document(doctorId).get().await()
                if (doc.exists()) {
                    _selectedDoctor.value = doc.toObject(Doctor::class.java)
                } else {
                    _message.value = "Bác sĩ không tồn tại"
                }
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Lỗi khi tải bác sĩ theo ID: ${e.message}")
            }
        }
    }

    // Fetch danh sách bác sĩ
    fun fetchDoctors() {
        viewModelScope.launch {
            try {
                _doctors.value = repository.getDoctor()
                Log.d("Firestore", "📥 Lấy danh sách bác sĩ thành công!")
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi tải danh sách bác sĩ!"
                Log.e("Firestore", "⚠️ Lỗi khi tải danh sách bác sĩ: ${e.message}")
            }
        }
    }

    fun saveDoctor(doctor: Doctor) {
        viewModelScope.launch {
            try {
                repository.saveDoctor(doctor)
                _message.value = "✅ Bác sĩ đã được lưu thành công!"
                fetchDoctors()
            } catch (e: Exception) {
                _message.value = "❌ Lưu bác sĩ thất bại: ${e.message}"
            }
        }
    }
    fun updateDoctor(doctor: Doctor) {
        viewModelScope.launch {
            try {
                repository.updateDoctor(doctor)
                _message.value = "✅ Cập nhật bác sĩ thành công!"
                fetchDoctors()
            } catch (e: Exception) {
                _message.value = "❌ Cập nhật bác sĩ thất bại: ${e.message}"
            }
        }
    }

    fun deleteDoctor(doctor: Doctor) {
        viewModelScope.launch {
            try {
                repository.deleteDoctor(doctor.doctorId)
                _message.value = "🗑️ Xóa bác sĩ thành công!"
                fetchDoctors()
            } catch (e: Exception) {
                _message.value = "❌ Xóa bác sĩ thất bại: ${e.message}"
            }
        }
    }


    fun updateDoctorWithImage(doctor: Doctor, imageUri: Uri?, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("doctor_images/${doctor.doctorId}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val updatedDoctor = doctor.copy(anh = uri.toString())
                        updateDoctor(updatedDoctor)
                        onSuccess()
                    }
                }
                .addOnFailureListener {
                    onFailure(it.message ?: "Lỗi không xác định khi upload ảnh.")
                }
        } else {
            updateDoctor(doctor)
            onSuccess()
        }
    }


    fun getDoctorById(doctorId: String) {
        viewModelScope.launch {
            val doctor = repository.getDoctorById(doctorId)
            _selectedDoctor.value = doctor
        }
    }

    fun getAverageRating(doctorId: String, onResult: (Double) -> Unit) {
        viewModelScope.launch {
            val rating = repository.calculateAverageRating(doctorId)
            onResult(rating)
        }
    }




}
