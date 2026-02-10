package com.whatsappstatussaver.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object StatusUtils {

    private fun getWhatsAppStatusFolder(context: Context): File? {
        // محاولات مختلفة للعثور على مجلد حالات الواتساب
        val possiblePaths = listOf(
            // WhatsApp
            File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses"),
            File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/Statuses"),
            // WhatsApp Business
            File(Environment.getExternalStorageDirectory(), "WhatsApp Business/Media/.Statuses"),
            File(Environment.getExternalStorageDirectory(), "WhatsApp Business/Media/Statuses"),
            // Android 11+ paths
            File(context.getExternalFilesDir(null), "WhatsApp/Media/.Statuses"),
            File(context.getExternalFilesDir(null), "WhatsApp/Media/Statuses")
        )

        return possiblePaths.find { it.exists() && it.isDirectory }
    }

    fun getStatusFiles(context: Context): List<File> {
        val statusFolder = getWhatsAppStatusFolder(context) ?: return emptyList()
        
        if (!statusFolder.exists() || !statusFolder.isDirectory) {
            return emptyList()
        }

        return try {
            statusFolder.listFiles()
                ?.filter { file ->
                    file.isFile && (isImageFile(file) || isVideoFile(file))
                }
                ?.sortedByDescending { it.lastModified() }
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun isImageFile(file: File): Boolean {
        val name = file.name.lowercase(Locale.getDefault())
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".gif")
    }

    private fun isVideoFile(file: File): Boolean {
        val name = file.name.lowercase(Locale.getDefault())
        return name.endsWith(".mp4") || name.endsWith(".3gp") || 
               name.endsWith(".mkv") || name.endsWith(".mov")
    }

    fun saveStatusToGallery(context: Context, statusFile: File): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        
        val fileName = if (isImageFile(statusFile)) {
            "Status_${timeStamp}.jpg"
        } else {
            "Status_${timeStamp}.mp4"
        }

        // إنشاء مجلد التطبيق في الصور
        val saveFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "WhatsApp Status Saver"
        )
        
        if (!saveFolder.exists()) {
            saveFolder.mkdirs()
        }

        val savedFile = File(saveFolder, fileName)
        
        // نسخ الملف
        copyFile(statusFile, savedFile)
        
        // إعلام الميديا سكانر بالملف الجديد
        notifyMediaScanner(context, savedFile)
        
        return savedFile
    }

    private fun copyFile(source: File, destination: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun notifyMediaScanner(context: Context, file: File) {
        val mediaScanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = android.net.Uri.fromFile(file)
        context.sendBroadcast(mediaScanIntent)
    }

    fun formatFileSize(file: File): String {
        val sizeInBytes = file.length()
        val kb = sizeInBytes / 1024.0
        val mb = kb / 1024.0

        return when {
            mb >= 1 -> String.format("%.1f MB", mb)
            kb >= 1 -> String.format("%.1f KB", kb)
            else -> "$sizeInBytes B"
        }
    }

    fun getFileType(file: File): String {
        return when {
            isImageFile(file) -> "image"
            isVideoFile(file) -> "video"
            else -> "unknown"
        }
    }
}
