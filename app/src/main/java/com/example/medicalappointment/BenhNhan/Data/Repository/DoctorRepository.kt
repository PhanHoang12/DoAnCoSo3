package com.example.medicalappointment.BenhNhan.Data.Repository


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class DoctorRepository {
    private val db = FirebaseFirestore.getInstance()
    private val doctorCollection = db.collection("doctors")
    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

}