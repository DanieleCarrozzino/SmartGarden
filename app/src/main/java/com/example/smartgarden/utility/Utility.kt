package com.example.smartgarden.utility

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.platform.LocalContext
import com.example.smartgarden.viewmodels.CameraViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Utility {

    companion object{
        fun convertHashMapToJsonString(hash : HashMap<String, Any>) : String{
            return Gson().toJson(hash).toString()
        }

        fun convertHashMapToJson(hash : HashMap<String, String>) : Any{
            return Gson().toJson(hash)
        }

        fun convertJsonToHashMap(json : String) : HashMap<String, Any>{
            val hashMap: HashMap<String, Any> = run {
                val mapType = object : TypeToken<HashMap<String, Any>>() {}.type
                Gson().fromJson(json, mapType)
            }
            return hashMap
        }

        fun generateKey(){

        }

        fun getCurrentDateTime(): String {
            val currentDateTime = Calendar.getInstance().time
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY) // Define your desired date-time format
            return formatter.format(currentDateTime)
        }

        fun getPath(array : List<Float>, size : Size) : Path{
            // Get max and min of the array to adapt the chart
            val min = array.min()
            val max = array.max() - min
            // delta to multiply to every elements inside the array
            val delta  = size.height / max
            // every x step to create a new point path
            val deltaX = size.width / (array.size - 1)

            return Path().apply {

                //Start position
                var prevOffset = Offset(0f, size.height)
                moveTo(prevOffset.x, prevOffset.y)

                // Start form the first arrayPosition
                lineTo(prevOffset.x, size.height - (array[0] - min) * delta)
                prevOffset = Offset(0f, size.height - (array[0] - min) * delta)

                for (i in 1 until array.size){
                    val y = size.height - (array[i] - min) * delta
                    val x = (i * deltaX)
                    quadraticBezierTo(prevOffset.x, prevOffset.y, (x + prevOffset.x) / 2, (y + prevOffset.y) / 2)
                    prevOffset = Offset(x, y)
                }
                lineTo(size.width, size.height - (array.last() - min) * delta)
                lineTo(size.width, size.height)
            }
        }

        fun getPath2(array : List<Float>, size : Size) : Path {

            println(array)

            // Get max and min of the array to adapt the chart
            val max = array.max()
            // delta to multiply to every elements inside the array
            val delta  = size.height / max
            // every x step to create a new point path
            val deltaX = size.width / (array.size - 1)

            return Path().apply {

                //Start position
                var prevOffset = Offset(0f, size.height)
                moveTo(prevOffset.x, prevOffset.y)

                // Start form the first arrayPosition
                lineTo(prevOffset.x, size.height - (array[0]) * delta)
                prevOffset = Offset(0f, size.height - (array[0]) * delta)

                for (i in 1 until array.size){
                    val y = size.height - (array[i]) * delta
                    val x = (i * deltaX)
                    quadraticBezierTo(prevOffset.x, prevOffset.y, (x + prevOffset.x) / 2, (y + prevOffset.y) / 2)
                    prevOffset = Offset(x, y)
                }
                lineTo(size.width, size.height - (array.last()) * delta)
                lineTo(size.width, size.height)
            }
        }

        fun getPathNodes(array : List<Float>, size : Size) : List<PathNode>{
            // Get max of the array to adapt the chart
            val max = array.max()
            // delta to multiply to every elements inside the array
            val delta  = size.height / max
            // every x step to create a new point path
            val deltaX = size.width / (array.size - 1)

            val list = mutableListOf<PathNode>()
            //Start position
            var prevOffset = Offset(0f, size.height)
            list.add(PathNode.MoveTo(prevOffset.x, prevOffset.y))

            // Start form the first arrayPosition
            list.add(PathNode.LineTo(prevOffset.x, size.height - array[0] * delta))

            for (i in 1 until array.size){
                val y = size.height - array[i] * delta
                val x = (i * deltaX)
                list.add(PathNode.QuadTo(prevOffset.x, prevOffset.y, (x + prevOffset.x) / 2, (y + prevOffset.y) / 2))
                prevOffset = Offset(x, y)
            }
            list.add(PathNode.LineTo(size.width, size.height - array.last() * delta))
            list.add(PathNode.LineTo(size.width, size.height))

            return list
        }


        fun lerp(start: List<PathNode>, stop: List<PathNode>, fraction: Float): List<PathNode> {
            return start.zip(stop) { a, b -> lerp(a, b, fraction) }
        }

        private fun lerp(start: PathNode, stop: PathNode, fraction: Float): PathNode {
            return when (start) {
                is PathNode.MoveTo -> {
                    require(stop is PathNode.MoveTo)
                    PathNode.MoveTo(
                        lerp(start.x, stop.x, fraction),
                        lerp(start.y, stop.y, fraction)
                    )
                }
                is PathNode.CurveTo -> {
                    require(stop is PathNode.CurveTo)
                    PathNode.CurveTo(
                        lerp(start.x1, stop.x1, fraction),
                        lerp(start.y1, stop.y1, fraction),
                        lerp(start.x2, stop.x2, fraction),
                        lerp(start.y2, stop.y2, fraction),
                        lerp(start.x3, stop.x3, fraction),
                        lerp(start.y3, stop.y3, fraction)
                    )
                }
                is PathNode.QuadTo -> {
                    require(stop is PathNode.QuadTo)
                    PathNode.QuadTo(
                        lerp(start.x1, stop.x1, fraction),
                        lerp(start.y1, stop.y1, fraction),
                        lerp(start.x2, stop.x2, fraction),
                        lerp(start.y2, stop.y2, fraction))
                }
                else -> {
                    return PathNode.LineTo(
                        lerp(0f, 0f, fraction),
                        lerp(0f, 0f, fraction)
                    )
                }
            }
        }

        private fun lerp(a: Float, b: Float, f: Float): Float {
            return a + f * (b - a)
        }

        fun getStatusBarSize(resources : Resources) : Int {
            val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(statusBarHeightId)
        }

        fun getNavigationBarSize(resources : Resources) : Int {
            val statusBarHeightId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(statusBarHeightId)
        }

        fun downloadFile(context : Context, url :String, filename : String){
            Log.d(CameraViewModel.TAG, "Starting download")
            //val file = File("${context.externalMediaDirs[0].absolutePath}/$filename")
            val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) // Visibility of the download Notification
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                //.setDestinationUri(Uri.fromFile(file))
                .setTitle(filename)
                .setDescription("Downloading")

            val downloadManager = context.getSystemService(ComponentActivity.DOWNLOAD_SERVICE) as DownloadManager?
            downloadManager?.enqueue(request)
        }

        fun stringToDate(dateString : String) : Date{
            return stringToDate(dateString, "yyyy-MM-dd_HH-mm-SSS")
        }

        fun stringToDate(dateString: String, format: String): Date {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            return sdf.parse(dateString) ?: Date()
        }

        fun convertMillisToDateString(millis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
            val date = Date(millis)
            return dateFormat.format(date)
        }
    }

}