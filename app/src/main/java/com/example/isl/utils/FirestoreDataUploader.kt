package com.example.isl.utils

import android.util.Log
import com.example.isl.data.MediaItem
import com.example.isl.data.MediaType
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreDataUploader {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    fun uploadSampleData() {
        val sampleData = listOf(
            MediaItem(
                id = "1",
                title = "Hello Sign",
                mediaUrl = "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop",
                thumbnailUrl = "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=200&h=200&fit=crop",
                mediaType = MediaType.IMAGE
            ),
            MediaItem(
                id = "2",
                title = "Thank You Sign", 
                mediaUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&h=400&fit=crop",
                thumbnailUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=200&h=200&fit=crop",
                mediaType = MediaType.IMAGE
            ),
            MediaItem(
                id = "3",
                title = "Goodbye Sign",
                mediaUrl = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop", 
                thumbnailUrl = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=200&h=200&fit=crop",
                mediaType = MediaType.IMAGE
            ),
            MediaItem(
                id = "4",
                title = "Basic Signs Tutorial",
                mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400&h=300&fit=crop",
                mediaType = MediaType.VIDEO
            ),
            MediaItem(
                id = "5", 
                title = "Alphabet Signs",
                mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400&h=300&fit=crop",
                mediaType = MediaType.VIDEO
            )
        )
        
        sampleData.forEach { item ->
            firestore.collection("mediaItems")
                .document(item.id)
                .set(item)
                .addOnSuccessListener {
                    Log.d("DataUploader", "Successfully uploaded: ${item.title}")
                }
                .addOnFailureListener { e ->
                    Log.e("DataUploader", "Failed to upload: ${item.title}", e)
                }
        }
    }
}