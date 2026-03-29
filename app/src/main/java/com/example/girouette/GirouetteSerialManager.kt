/**
 * FILE: GirouetteSerialManager.kt
 * PURPOSE: Handles the physical RS232 connection with Root/High-Permission support.
 */
package com.example.girouette

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GirouetteSerialManager {

    companion object {
        private const val TAG = "GirouetteSerialManager"
        private const val BAUD_RATE = 9600 
    }

    private var outputStream: FileOutputStream? = null

    fun connect(portPath: String = "/dev/ttyS8"): Boolean {
        try {
            val device = File(portPath)
            
            // 1. Try to fix permissions using SU (like ADB shell)
            try {
                Log.d(TAG, "Requesting root access to chmod $portPath")
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "chmod 666 $portPath"))
                process.waitFor()
            } catch (e: Exception) {
                Log.w(TAG, "Root/SU access failed or not available.")
            }

            // 2. Set Baud Rate
            try {
                Runtime.getRuntime().exec("stty -F $portPath $BAUD_RATE")
            } catch (e: Exception) {
                Log.w(TAG, "stty failed: ${e.message}")
            }

            if (device.exists() && device.canWrite()) {
                outputStream = FileOutputStream(device)
                Log.d(TAG, "SUCCESS: Port $portPath is open and ready.")
                return true
            } else {
                Log.e(TAG, "FAILED: Device does not exist or permission denied for $portPath")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connect Error: ${e.message}")
            return false
        }
    }

    fun disconnect() {
        try {
            outputStream?.close()
            outputStream = null
        } catch (e: IOException) {
            Log.e(TAG, "Disconnect Error: ${e.message}")
        }
    }

    /**
     * Sends data. Returns TRUE if successful, FALSE if connection is missing or hardware failed.
     */
    suspend fun sendData(state: GirouetteUiState): Boolean {
        return withContext(Dispatchers.IO) {
            // Check if connection exists. If not, try to reconnect once.
            if (outputStream == null) {
                Log.w(TAG, "OutputStream is null. Attempting to reconnect...")
                if (!connect()) return@withContext false
            }
            
            val fullMessage = GirouetteProtocol.buildMessage(state)
            val frames = fullMessage.toList().chunked(8) 

            try {
                frames.forEach { frame ->
                    outputStream?.write(frame.toByteArray())
                    outputStream?.flush()
                    delay(50) 
                }
                Log.i(TAG, "Data sent successfully.")
                true
            } catch (e: IOException) {
                Log.e(TAG, "Write Error: ${e.message}")
                false
            }
        }
    }
}
