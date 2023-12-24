package com.example.smartgarden.manager

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
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

    val password = "password"
    lateinit var raspberryIp : String
    lateinit var raspberryUsername : String
    lateinit var sourceFilePath : String // File to be transferred from local to remote
    val destinationDirectory = "/path/to/destination/directory" // Destination directory on remote

    /**
     * @param host raspberry ip
     * */
    suspend fun sendConfigFile() {
        withContext(Dispatchers.IO){
            try {
                val jsch = JSch()
                val session: Session = jsch.getSession(raspberryUsername, raspberryIp, 22)
                session.setPassword(password)

                // Avoid asking for key confirmation
                session.setConfig("StrictHostKeyChecking", "no")
                session.connect()

                // Connected
                callback(RaspberryStatus.START_CONFIGURED, "")

                // Establish a channel for SCP
                val channel = session.openChannel("exec") as ChannelExec
                val command = "scp -t $destinationDirectory"
                (channel as ChannelExec).setCommand(command)

                // Get I/O streams for sending/receiving data
                val out = channel.outputStream
                val `in` = channel.inputStream

                channel.connect()

                // Construct the SCP command to send a file
                val header = "C0644 ${File(sourceFilePath).length()} "
                val commandToSend = "scp -t $destinationDirectory"

                // Send the command header
                out.write("$header$sourceFilePath\n".toByteArray())
                out.flush()

                // Transfer file content
                val fis = FileInputStream(sourceFilePath)
                val bis = BufferedInputStream(fis)
                val bos = BufferedOutputStream(out)

                val buf = ByteArray(1024)
                var len: Int
                while (bis.read(buf).also { len = it } > 0) {
                    bos.write(buf, 0, len)
                }

                bis.close()
                fis.close()
                bos.close()

                channel.disconnect()
                session.disconnect()

                println("File transferred successfully")
                callback(RaspberryStatus.END_CONFIGURED, "")

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