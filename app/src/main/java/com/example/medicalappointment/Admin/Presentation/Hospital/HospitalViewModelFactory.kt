package com.example.medicalappointment.Admin.Presentation.Hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalapp.Admin.Presentation.Hospitals.HospitalViewModel
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository

class HospitalViewModelFactory(
    private val repository: HospitalRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HospitalViewModel::class.java)) {
            HospitalViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
