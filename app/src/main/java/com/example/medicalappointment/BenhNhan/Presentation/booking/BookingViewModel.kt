package com.example.medicalappointment.BenhNhan.Presentation.booking

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.medicalappointment.Admin.Data.Model.Notification
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

    fun confirmBooking(
        BookingID: String,
        patientId: String,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings")
            .document(BookingID)
            .update("trangThai", "Đã xác nhận")
            .addOnSuccessListener {
                val notification = Notification(
                    userId = patientId,
                    title = "Lịch hẹn đã được xác nhận",
                    message = "Bác sĩ đã xác nhận lịch hẹn của bạn.",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
//                onSuccess()
//                currentDoctorId?.let { loadBookings(it) }
                db.collection("notifications")
                    .add(notification)
                    .addOnSuccessListener {
                        onSuccess()
                        currentDoctorId?.let { loadBookings(it) }
                    }

                    .addOnFailureListener { exception ->
                        onFailure("Xác nhận thất bại: ${exception.message ?: "Không rõ lý do"}")
                    }
            }
    }

    // Xoá lịch hẹn
    fun deleteBooking(
        BookingID: String,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
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
    fun rejectBooking(
        BookingID: String,
        patientId: String,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings")
            .document(BookingID)
            .update("trangThai", "Đã từ chối")
            .addOnSuccessListener {
                // Gửi thông báo tới patients
                val notification = Notification(
                    userId = patientId,
                    title = "Lịch hẹn bị từ chối",
                    message = "Bác sĩ đã từ chối lịch hẹn của bạn.",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                db.collection("notifications")
                    .add(notification)
                    .addOnSuccessListener {
                        onSuccess()
                        currentDoctorId?.let { loadBookings(it) }
                    }
            }
            .addOnFailureListener { exception ->
                onFailure("Từ chối thất bại: ${exception.message ?: "Không rõ lý do"}")
            }
    }

}