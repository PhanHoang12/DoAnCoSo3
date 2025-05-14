package com.example.medicalappointment.Admin.Presentation.Specialty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicalapp.Admin.Presentation.Specialty.SpecialtyViewModel
import com.example.medicalappointment.Admin.Data.Repository.SpecialtyRepository

class SpecialtyViewModelFactory(private val repository: SpecialtyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SpecialtyViewModel(repository) as T
    }
}
