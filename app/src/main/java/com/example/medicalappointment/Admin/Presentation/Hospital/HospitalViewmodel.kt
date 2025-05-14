package com.example.medicalapp.Admin.Presentation.Hospitals

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalapp.Admin.Data.Model.Hospital
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HospitalViewModel(private val repository : HospitalRepository) : ViewModel() {



    private val _hospitals = MutableStateFlow<List<Hospital>>(emptyList())
    val hospitals: StateFlow<List<Hospital>> get() = _hospitals

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    private val _selectedHospital = MutableStateFlow<Hospital?>(null)
    val selectedHospital: StateFlow<Hospital?> get() = _selectedHospital

    init {
        fetchHospitals()
    }

    fun fetchHospitalById(hospitalId: String) {
        viewModelScope.launch {
            try {
                _selectedHospital.value = repository.getHospitalById(hospitalId)
            } catch (e: Exception) {
                Log.e("HospitalViewModel", "Error fetching hospital: ${e.message}")
            }
        }
    }

    fun fetchHospitals() {
        viewModelScope.launch {
            try {
                Log.d("HospitalViewModel", "📥 Bắt đầu tải danh sách bệnh viện...")
                _hospitals.value = repository.getAllHospitals()
                Log.d(
                    "HospitalViewModel",
                    "📥 Lấy danh sách bệnh viện thành công! Tổng số: ${_hospitals.value.size}"
                )
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi tải danh sách bệnh viện!"
                Log.e("HospitalViewModel", "⚠️ Lỗi khi tải danh sách bệnh viện: ${e.message}", e)
            }
        }
    }

    fun saveHospital(hospital: Hospital) {
        viewModelScope.launch {
            try {
                Log.d("HospitalViewModel", "📤 Bắt đầu lưu bệnh viện: ${hospital.tenBV}")
                val success = repository.addHospital(hospital)
                if (success) {
                    _message.value = "🏥 Bệnh viện đã được lưu thành công!"
                    Log.d("HospitalViewModel", "📤 Lưu bệnh viện thành công: ${hospital.tenBV}")
                    fetchHospitals() // Refresh danh sách bệnh viện
                } else {
                    _message.value = "❌ Bệnh viện đã tồn tại hoặc lỗi khi lưu!"
                    Log.d("HospitalViewModel", "❌ Không thể lưu bệnh viện, có thể đã tồn tại.")
                }
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi lưu bệnh viện: ${e.message}"
                Log.e("HospitalViewModel", "❌ Lỗi khi lưu bệnh viện", e)
            }
        }
    }

    suspend fun uploadImage(imageUri: Uri, hospitalId: String): String? {
        return try {
            Log.d("HospitalViewModel", "📸 Bắt đầu upload ảnh cho bệnh viện với ID: $hospitalId")
            val url = repository.uploadImage(hospitalId, imageUri)
            Log.d("HospitalViewModel", "📸 Upload ảnh thành công! URL: $url")
            url
        } catch (e: Exception) {
            Log.e("HospitalViewModel", "❌ Lỗi khi upload ảnh cho bệnh viện ID: $hospitalId", e)
            null
        }
    }

    fun updateHospital(hospital: Hospital, newImageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                Log.d("HospitalViewModel", "✏️ Bắt đầu cập nhật bệnh viện: ${hospital.tenBV}")
                val imageUrl = newImageUri?.let {
                    // Chờ kết quả từ hàm suspend uploadImage
                    uploadImage(it, hospital.id)
                } ?: hospital.anh
                val updatedHospital = hospital.copy(anh = imageUrl ?: hospital.anh)
                val success = repository.updateHospital(updatedHospital)
                if (success) {
                    _message.value = "✏️ Bệnh viện đã được cập nhật thành công!"
                    Log.d("HospitalViewModel", "✏️ Cập nhật bệnh viện thành công: ${hospital.tenBV}")
                    fetchHospitals()
                } else {
                    _message.value = "❌ Không thể cập nhật bệnh viện!"
                    Log.d("HospitalViewModel", "❌ Không thể cập nhật bệnh viện: ${hospital.tenBV}")
                }
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi cập nhật bệnh viện: ${e.message}"
                Log.e("HospitalViewModel", "❌ Lỗi khi cập nhật bệnh viện", e)
            }
        }
    }



    fun deleteHospital(hospital: Hospital) {
        viewModelScope.launch {
            try {
                Log.d("HospitalViewModel", "🗑️ Bắt đầu xóa bệnh viện: ${hospital.tenBV}")
                val success = repository.deleteHospital(hospital.id)
                if (success) {
                    _message.value = "🗑️ Bệnh viện đã được xóa thành công!"
                    Log.d("HospitalViewModel", "🗑️ Xóa bệnh viện thành công: ${hospital.tenBV}")
                    fetchHospitals() // Refresh danh sách bệnh viện
                } else {
                    _message.value = "❌ Không thể xóa bệnh viện!"
                    Log.d("HospitalViewModel", "❌ Không thể xóa bệnh viện: ${hospital.tenBV}")
                }
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi xóa bệnh viện: ${e.message}"
                Log.e("HospitalViewModel", "❌ Lỗi khi xóa bệnh viện", e)
            }

        }
    }


}
