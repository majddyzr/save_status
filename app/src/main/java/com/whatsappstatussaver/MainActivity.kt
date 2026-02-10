package com.whatsappstatussaver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whatsappstatussaver.adapters.StatusAdapter
import com.whatsappstatussaver.utils.StatusUtils
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var statusAdapter: StatusAdapter
    private val STORAGE_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        checkPermissions()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        statusAdapter = StatusAdapter(this) { statusFile ->
            saveStatus(statusFile)
        }
        recyclerView.adapter = statusAdapter
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - need media permissions
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    loadStatuses()
                }
                else -> {
                    requestMediaPermissions()
                }
            }
        } else {
            // Android 12 and below
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    loadStatuses()
                }
                else -> {
                    requestStoragePermission()
                }
            }
        }
    }

    private fun requestMediaPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            ),
            STORAGE_PERMISSION_CODE
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadStatuses()
            } else {
                Toast.makeText(this, "نحتاج إلى صلاحية الوصول للتخزين", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadStatuses() {
        try {
            val statusFiles = StatusUtils.getStatusFiles(this)
            statusAdapter.updateStatuses(statusFiles)
            
            if (statusFiles.isEmpty()) {
                Toast.makeText(this, "لا توجد حالات متاحة", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "خطأ في تحميل الحالات: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveStatus(statusFile: File) {
        try {
            val savedFile = StatusUtils.saveStatusToGallery(this, statusFile)
            Toast.makeText(this, "تم حفظ الحالة بنجاح", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "خطأ في حفظ الحالة: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
