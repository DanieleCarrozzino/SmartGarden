package com.example.smartgarden.manager

import android.content.Context
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject


class RaspberryConnectionManager @Inject constructor(
    @ApplicationContext context : Context) : ImageAnalysis.Analyzer {

    private lateinit var scanner : BarcodeScanner
    private lateinit var callback : (RaspberryStatus, String?) -> Unit
    private var qrScanned = false

    enum class RaspberryStatus{
        SCANNED,
        START_CONFIGURED,
        END_CONFIGURED,
        FINISHED,
        CLOSE,
    }

    val password = "daniele"
    lateinit var raspberryIp : String
    lateinit var raspberryUsername : String
    lateinit var sourceFilePath : String // File to be transferred from local to remote
    val destinationDirectory = "/home/daniele/Projects/RaspGarden/configuration/files/" // Destination directory on remote

    /**
     * @param host raspberry ip
     * */
    suspend fun sendConfigFile() {
        withContext(Dispatchers.IO){
            try {

                // Layout
                delay(2000)
                callback(RaspberryStatus.START_CONFIGURED, "")

                val jsch = JSch()
                val session: Session = jsch.getSession(raspberryUsername, raspberryIp, 22)
                session.setPassword(password)

                // Avoid asking for key confirmation
                session.setConfig("StrictHostKeyChecking", "no")
                session.connect()

                //SFTP setup
                val channel: Channel = session.openChannel("sftp")
                channel.connect()

                val channelsftp = channel as ChannelSftp
                channelsftp.cd(destinationDirectory)

                // Layout
                delay(2000)
                callback(RaspberryStatus.END_CONFIGURED, "")

                val file = File(sourceFilePath)
                channelsftp.put(FileInputStream(file), file.name)

                channel.disconnect()

                // Layout
                delay(2000)
                callback(RaspberryStatus.FINISHED, "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initializeScanner(callback : (RaspberryStatus, String?) -> Unit) {
        qrScanned = false
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        this.callback = callback
        scanner = BarcodeScanning.getClient(options)
    }

    @ExperimentalGetImage @Suppress("EXPERIMENTAL_API_USAGE")
    override fun analyze(imageProxy: ImageProxy) {
        if(qrScanned) return

        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if(barcodes.size > 0) {
                            Log.d("Qr code result", barcodes[0].rawValue.toString())
                            if (barcodes[0].rawValue.toString().startsWith("rasp_code:")) {
                                qrScanned = true
                                callback(
                                    RaspberryStatus.SCANNED,
                                    barcodes[0].rawValue.toString().substringAfter("rasp_code:")
                                )
                            }
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
        catch(ex : Exception){

        }
        finally {
            imageProxy.close()
        }
    }

}