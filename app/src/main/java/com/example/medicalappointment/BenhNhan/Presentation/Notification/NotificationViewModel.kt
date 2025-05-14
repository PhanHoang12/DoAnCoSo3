package com.example.medicalappointment.BenhNhan.Presentation.Notification

import androidx.compose.runtime.mutableStateOf
import com.example.medicalappointment.Admin.Data.Model.Notification
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class NotificationViewModel : ViewModel() {

    private val _notifications = mutableStateOf<List<Notification>>(emptyList())
    val notifications: State<List<Notification>> = _notifications

    private val db = Firebase.firestore

    fun loadNotificationsForUser(userId: String) {
        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    _notifications.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Notification::class.java)?.copy(userId = doc.id)
                    }
                }
            }
    }

    fun markNotificationAsRead(notificationId: String) {
        db.collection("notifications")
            .document(notificationId)
            .update("isRead", true)
    }
}
