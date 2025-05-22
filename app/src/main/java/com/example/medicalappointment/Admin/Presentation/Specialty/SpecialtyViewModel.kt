package com.example.medicalapp.Admin.Presentation.Specialty

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalapp.Admin.Data.Model.Specialty
import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpecialtyViewModel(private val repository: SpecialtyRepository) : ViewModel() {
    private val _specialtys = MutableStateFlow<List<Specialty>>(emptyList())
    val specialtys: StateFlow<List<Specialty>> get() = _specialtys

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    init {
        fetchSpecialtys()
    }

    fun fetchSpecialtys() {
        viewModelScope.launch {
            try {
                Log.d("SpecialtyViewModel", "📥 Bắt đầu tải danh sách chuyên khoa...")
                _specialtys.value = repository.getSpecialty()
                Log.d("SpecialtyViewModel", "📥 Lấy danh sách chuyên khoa thành công! Tổng số: ${_specialtys.value.size}")
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi tải danh sách chuyên khoa!"
                Log.e("SpecialtyViewModel", "⚠️ Lỗi khi tải chuyên khoa: ${e.message}", e)
            }
        }
    }

    fun saveSpecialty(specialty: Specialty) {
        val db = FirebaseFirestore.getInstance()
        val specialtyRef = db.collection("specialtys")

        val data = hashMapOf(
            "id" to specialty.id,
            "name" to specialty.name,
            "imageUrl" to specialty.imageUrl
        )

        Log.d("SpecialtyViewModel", "📤 Bắt đầu lưu chuyên khoa: ${specialty.name}")

        specialtyRef.document(specialty.id).set(data)
            .addOnSuccessListener {
                _message.value = "✅ Chuyên khoa đã được lưu thành công!"
                Log.d("SpecialtyViewModel", "📤 Lưu chuyên khoa thành công: ${specialty.name}")
                fetchSpecialtys()
            }
            .addOnFailureListener {
                _message.value = "❌ Lưu chuyên khoa thất bại: ${it.message}"
                Log.e("SpecialtyViewModel", "❌ Lưu chuyên khoa thất bại: ${it.message}", it)
            }
    }
    fun updateSpecialty(specialty: Specialty) {
        val db = FirebaseFirestore.getInstance()
        val specialtyRef = db.collection("specialtys")

        val data = hashMapOf(
            "id" to specialty.id,
            "name" to specialty.name,
            "imageUrl" to specialty.imageUrl
        )

        Log.d("SpecialtyViewModel", "✏️ Bắt đầu cập nhật chuyên khoa: ${specialty.name}")

        specialtyRef.document(specialty.id).update(data as Map<String, Any>)
            .addOnSuccessListener {
                _message.value = "✅ Cập nhật chuyên khoa thành công!"
                Log.d("SpecialtyViewModel", "✅ Cập nhật thành công chuyên khoa: ${specialty.name}")
                fetchSpecialtys()
            }
            .addOnFailureListener {
                _message.value = "❌ Cập nhật chuyên khoa thất bại: ${it.message}"
                Log.e("SpecialtyViewModel", "❌ Cập nhật chuyên khoa thất bại: ${it.message}", it)
            }
    }

    fun deleteSpecialty(specialty: Specialty) {
        val db = FirebaseFirestore.getInstance()
        val specialtyRef = db.collection("specialtys")

        Log.d("SpecialtyViewModel", "🗑️ Bắt đầu xóa chuyên khoa với ID: ${specialty.name}")

        specialtyRef.document(specialty.id).delete()
            .addOnSuccessListener {
                _message.value = "🗑️ Xóa chuyên khoa thành công!"
                Log.d("SpecialtyViewModel", "🗑️ Đã xóa chuyên khoa có ID: ${specialty.name}")
                fetchSpecialtys()
            }
            .addOnFailureListener {
                _message.value = "❌ Xóa chuyên khoa thất bại: ${it.message}"
                Log.e("SpecialtyViewModel", "❌ Xóa chuyên khoa thất bại: ${it.message}", it)
            }
    }


}
