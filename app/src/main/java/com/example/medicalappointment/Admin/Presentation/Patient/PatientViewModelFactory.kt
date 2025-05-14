package com.example.medicalappointment.Admin.Presentation.Patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalapp.Admin.Data.Repository.PatientRepository
import com.example.medicalapp.Admin.Presentation.Patient.PatientViewModel

class PatientViewModelFactory(
    private val repository: PatientRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            return PatientViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
