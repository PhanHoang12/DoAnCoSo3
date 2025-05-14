package com.example.medicalappointment.BenhNhan.Data.Repository

import com.example.medicalapp.Admin.Data.Model.Doctor
import com.example.medicalappointment.BenhNhan.Data.Model.BookingInfo
import com.example.medicalappointment.BenhNhan.Presentation.Patient.Booking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class BookingRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // StateFlow cho BookingInfo
    private val _bookingInfo = MutableStateFlow(BookingInfo())
    val bookingInfo: StateFlow<BookingInfo> = _bookingInfo

    // StateFlow cho Doctor
    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    // Lấy danh sách ngày khám khả dụng (giả lập 7 ngày kế tiếp)
    fun getAvailableDates(): List<LocalDate> {
        return List(7) { index -> LocalDate.now().plusDays(index.toLong()) }
    }

    // Lấy danh sách giờ khám dựa vào buổi sáng/chiều
    fun getTimes(period: String): List<String> {
        return if (period == "morning") {
            listOf("08:00", "09:00", "10:00", "11:00")
        } else {
            listOf("14:00", "15:00", "16:00", "17:00")
        }
    }

    // Cập nhật ngày khám
    fun updateDate(date: LocalDate) {
        _bookingInfo.value = _bookingInfo.value.copy(selectedDate = date)
    }

    // Cập nhật buổi (sáng/chiều)
    fun updatePeriod(period: String) {
        _bookingInfo.value = _bookingInfo.value.copy(selectedPeriod = period)
    }

    // Cập nhật giờ khám
    fun updateTime(time: String) {
        _bookingInfo.value = _bookingInfo.value.copy(selectedTime = time)
    }

    // 👉 Hàm fetch bác sĩ từ Firestore theo doctorId
    suspend fun fetchDoctorById(doctorId: String) {
        try {
            val snapshot = firestore.collection("doctors").document(doctorId).get().await()
            val doctor = snapshot.toObject(Doctor::class.java)
            _doctor.value = doctor
        } catch (e: Exception) {
            e.printStackTrace()
            _doctor.value = null
        }
    }
    fun getBookingsByDoctorId(doctorId: String, onResult: (List<Booking>) -> Unit) {
        firestore.collection("bookings")
            .whereEqualTo("IdBacSi", doctorId)
            .get()
            .addOnSuccessListener { result ->
                val bookings = result.mapNotNull { doc ->
                    doc.toObject(Booking::class.java)?.apply {
                        BookingID = doc.id // ⚠️ Gán ID document để các thao tác sau không lỗi
                    }
                }
                onResult(bookings)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}
