package com.example.medicalapp.Admin.Presentation.Patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalapp.Admin.Data.Repository.PatientRepository
import com.example.medicalappointment.BenhNhan.Data.Model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patient = MutableStateFlow<List<Patient>>(emptyList())
    val patient: StateFlow<List<Patient>> get() = _patient

    private val _selectedPatient = MutableStateFlow<Patient?>(null)
    val selectedPatient: StateFlow<Patient?> = _selectedPatient

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> get() = _message

    fun getPatientById(patientId: String) {
        repository.getPatientById(patientId) {
            _selectedPatient.value = it
        }
    }
            // Lấy danh sách bệnh nhân từ Firebase
    fun fetchPatient() {
        viewModelScope.launch {
            try {
                _patient.value = repository.getPatientList()
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi tải danh sách bệnh nhân!"
            }
        }
    }

    // Xóa bệnh nhân khỏi Firestore
    fun deletePatient(userId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deletePatient(userId)
                if (result) {
                    _message.value = "✅ Xóa bệnh nhân thành công!"
                    fetchPatient()
                } else {
                    _message.value = "❌ Lỗi khi xóa bệnh nhân!"
                }
            } catch (e: Exception) {
                _message.value = "❌ Lỗi khi xóa bệnh nhân!"
            }
        }
    }
}
