package com.example.medicalappointment.Chat.Data.Reponsitory

import android.net.Uri
import com.example.medicalappointment.Chat.Data.Model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    fun sendMessage(message: Message, onComplete: (Boolean) -> Unit) {
        firestore.collection("chats")
            .document(generateChatId(message.senderId, message.receiverId))
            .collection("messages")
            .add(message)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getMessages(senderId: String, receiverId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(generateChatId(senderId, receiverId))
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = value?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    private fun generateChatId(senderId: String, receiverId: String): String {
        return if (senderId < receiverId) "$senderId-$receiverId" else "$receiverId-$senderId"
    }

    suspend fun uploadImage(uri: Uri): String {
        val ref = storage.reference.child("chat_images/${UUID.randomUUID()}")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
