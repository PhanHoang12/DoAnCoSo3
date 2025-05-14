package com.example.medicalapp.Admin.Presentation.Doctors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository

class DoctorViewModelFactory(private val repository: DoctorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DoctorViewModel(repository) as T
    }
}
