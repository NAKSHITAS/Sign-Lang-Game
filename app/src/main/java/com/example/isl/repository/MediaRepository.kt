package com.example.isl.repository

import android.util.Log
import com.example.isl.data.MediaItem
import com.example.isl.data.MediaType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MediaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val mediaCollection = FirebaseFirestore.getInstance().collection("mediaItems")


    suspend fun getMediaItems(): List<MediaItem> {
        return try {
            val snapshot = mediaCollection.get().await()
            Log.d("MediaRepo", "Docs found: ${snapshot.size()}")

            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.getString("id") ?: ""
                    val title = doc.getString("title") ?: ""
                    val mediaUrl = doc.getString("mediaUrl") ?: ""
                    val thumbnailUrl = doc.getString("thumbnailUrl") ?: ""
                    val mediaTypeString = doc.getString("mediaType") ?: "IMAGE"
                    val mediaType = try {
                        MediaType.valueOf(mediaTypeString.uppercase())
                    } catch (e: IllegalArgumentException) {
                        Log.w("MediaRepo", "Unknown media type: $mediaTypeString, defaulting to IMAGE")
                        MediaType.IMAGE
                    }

                    MediaItem(
                        id = id,
                        title = title,
                        mediaUrl = mediaUrl,
                        thumbnailUrl = thumbnailUrl,
                        mediaType = mediaType
                    )
                } catch (e: Exception) {
                    Log.e("MediaRepo", "Error parsing document: ${doc.id}", e)
                    null
                }
            }

            Log.d("MediaRepo", "Parsed items: ${items.size}")
            items

        } catch (e: Exception) {
            Log.e("MediaRepo", "Failed to fetch media items", e)
            emptyList()
        }
    }
}

