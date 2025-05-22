package com.example.medicalappointment.Admin.Presentation.Specialty

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medicalapp.Admin.Data.Model.Specialty
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun SpecialtyDropdown(
    specialties: List<Specialty>,
    selectedSpecialty: Specialty?,
    onSpecialtySelected: (Specialty) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = selectedSpecialty?.name ?: "Chọn chuyên khoa"

    Box{
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .clickable{ expanded = true }
                .background(Color.LightGray)
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            specialties.forEach { specialty ->
                DropdownMenuItem(
                    onClick = {
                        onSpecialtySelected(specialty)
                        expanded = false
                    }
                ) {
                    Text(text = specialty.name)
                }
            }
        }
    }
}
