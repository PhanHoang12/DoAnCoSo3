package com.example.medicalappointment.BenhNhan.Presentation.Patient

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalapp.BenhNhan.Repository.PatientRepository
import com.example.medicalappointment.BenhNhan.Data.Model.Patient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PatientViewModel : ViewModel() {
    private val repository = PatientRepository()

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Add success flag
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        loadPatient()
    }

    fun loadPatient() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getPatientInfo()
            result
                .onSuccess { _patient.value = it }
                .onFailure { _error.value = it.message }
            _loading.value = false
        }
    }

    fun updatePatientInfo(
        hoTen: String,
        sdt: String,
        tieusu: String,
        gioitinh: Boolean,
        ngaysinhStr: String
    ) {
        val current = _patient.value ?: return
        viewModelScope.launch {
            _loading.value = true
            _saveSuccess.value = false // Reset trước khi xử lý

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date: Date = try {
                sdf.parse(ngaysinhStr) ?: current.ngaysinh
            } catch (e: Exception) {
                _error.value = "Ngày sinh không hợp lệ"
                _loading.value = false
                return@launch
            }

            val result = repository.createOrUpdatePatient(
                hoTen = hoTen,
                gioitinh = gioitinh,
                ngaysinh = date,
                tieusu = tieusu,
                sdt = sdt,
                avatarUri = null
            )

            if (result.isSuccess) {
                loadPatient()
                _error.value = null
                _saveSuccess.value = true //  Set flag true nếu thành công
            } else {
                _error.value = result.exceptionOrNull()?.message
            }

            _loading.value = false
        }
    }

    fun uploadAvatar(uri: Uri) {
        val current = _patient.value ?: return
        viewModelScope.launch {
            _loading.value = true
            val result = repository.createOrUpdatePatient(
                hoTen = current.hoTen,
                gioitinh = current.gioitinh,
                ngaysinh = current.ngaysinh,
                tieusu = current.tieusu,
                sdt = current.sdt,
                avatarUri = uri
            )
            if (result.isSuccess) {
                loadPatient()
                _error.value = null
                _saveSuccess.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _loading.value = false
        }
    }

    //  Dùng trong Composable để reset trạng thái
    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
    fun logout(){
        FirebaseAuth.getInstance().signOut();
    }

}
