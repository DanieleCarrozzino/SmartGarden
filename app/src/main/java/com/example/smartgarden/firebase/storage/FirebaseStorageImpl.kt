package com.example.smartgarden.firebase.storage

import android.graphics.Bitmap
import com.example.smartgarden.utility.Utility
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageImpl @Inject constructor() : FirebaseStorageInterface {

    private val storage = Firebase.storage

    override suspend fun getLastImageUrl(code : String) : String {

        val ref     = storage.reference.child("$code/Pictures/")
        val list    = ref.listAll().await()

        // Return an empty url if the list of images
        // is empty
        if(list.items.size == 0){
            return ""
        }

        // Get the last photo
        val imageUrl = list.items.last().downloadUrl.await()
        println("Image URL: $imageUrl")

        return imageUrl.toString()
    }

    override suspend fun getTimeLapsUrl(code : String) : Pair<String, String> {

        val ref     = storage.reference.child("$code/Timelaps/")
        val list    = ref.listAll().await()

        // Return an empty string if the
        // timelaps is not available or not present
        if(list.items.size == 0)
            return Pair("", "")

        val timelapseUrl    = list.items[0].downloadUrl.await()
        val metadata        = list.items[0].metadata.await()

        return Pair(timelapseUrl.toString(), Utility.convertMillisToDateString(metadata.creationTimeMillis))
    }

    override suspend fun getLastTakenImageUrl(code: String): Pair<String, String> {
        val ref     = storage.reference.child("$code/InstantPictures/")
        val list    = ref.listAll().await()

        // Return an empty url if the list of images
        // is empty
        if(list.items.size == 0){
            return Pair("", "")
        }

        // Get the last photo
        val imageUrl    = list.items.last().downloadUrl.await()
        val name        = list.items.last().name
        return Pair(name, imageUrl.toString())
    }
}