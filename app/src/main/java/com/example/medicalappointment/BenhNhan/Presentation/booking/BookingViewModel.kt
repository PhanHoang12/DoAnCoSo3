package com.example.medicalappointment.BenhNhan.Presentation.booking

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.medicalappointment.BenhNhan.Data.Repository.BookingRepository
import com.example.medicalappointment.BenhNhan.Presentation.Patient.Booking
import com.google.firebase.firestore.FirebaseFirestore

class BookingViewModel : ViewModel() {
    private val repository = BookingRepository()

    private val _bookingList = mutableStateOf<List<Booking>>(emptyList())
    val bookingList: State<List<Booking>> = _bookingList

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var currentDoctorId: String? = null

    // Tải danh sách cuộc hẹn cho bác sĩ
    fun loadBookings(doctorId: String) {
        loading = true
        error = null
        currentDoctorId = doctorId
        repository.getBookingsByDoctorId(doctorId) { bookings ->
            _bookingList.value = bookings
            loading = false
        }
    }

    // Xác nhận lịch hẹn
    fun confirmBooking(BookingID: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        FirebaseFirestore.getInstance()
            .collection("bookings")
            .document(BookingID)
            .update("trangThai", "Đã xác nhận")
            .addOnSuccessListener {
                onSuccess()
                currentDoctorId?.let { loadBookings(it) }
            }
            .addOnFailureListener { exception ->
                onFailure("Xác nhận thất bại: ${exception.message ?: "Không rõ lý do"}")
            }
    }

    // Xoá lịch hẹn
    fun deleteBooking(BookingID: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        FirebaseFirestore.getInstance()
            .collection("bookings")
            .document(BookingID)
            .delete()
            .addOnSuccessListener {
                onSuccess()
                currentDoctorId?.let { loadBookings(it) }  // Làm mới danh sách sau khi xoá
            }
            .addOnFailureListener { exception ->
                onFailure("Xoá thất bại: ${exception.message ?: "Không rõ lý do"}")
            }
    }

    // Từ chối lịch hẹn
    fun rejectBooking(BookingID: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        FirebaseFirestore.getInstance()
            .collection("bookings")
            .document(BookingID)
            .update("trangThai", "Đã từ chối")
            .addOnSuccessListener {
                onSuccess()
                currentDoctorId?.let { loadBookings(it) }  // Làm mới danh sách sau khi từ chối
            }
            .addOnFailureListener { exception ->
                onFailure("Từ chối thất bại: ${exception.message ?: "Không rõ lý do"}")
            }
    }
}
