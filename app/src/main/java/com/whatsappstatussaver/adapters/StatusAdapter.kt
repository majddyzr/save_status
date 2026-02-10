package com.whatsappstatussaver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whatsappstatussaver.R
import com.whatsappstatussaver.utils.StatusUtils
import java.io.File

class StatusAdapter(
    private val context: Context,
    private val onSaveClick: (File) -> Unit
) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    private var statusFiles: List<File> = emptyList()

    fun updateStatuses(newStatuses: List<File>) {
        statusFiles = newStatuses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(statusFiles[position])
    }

    override fun getItemCount(): Int = statusFiles.size

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val videoIcon: ImageView = itemView.findViewById(R.id.videoIcon)
        private val saveButton: ImageView = itemView.findViewById(R.id.saveButton)
        private val fileSize: TextView = itemView.findViewById(R.id.fileSize)

        fun bind(statusFile: File) {
            val fileType = StatusUtils.getFileType(statusFile)
            
            // عرض الصورة أو الفيديو
            if (fileType == "image") {
                Glide.with(context)
                    .load(statusFile)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView)
                videoIcon.visibility = View.GONE
            } else {
                // للفيديو، عرض الإطار الأول
                Glide.with(context)
                    .load(statusFile)
                    .placeholder(R.drawable.placeholder_video)
                    .error(R.drawable.error_image)
                    .into(imageView)
                videoIcon.visibility = View.VISIBLE
            }

            // عرض حجم الملف
            fileSize.text = StatusUtils.formatFileSize(statusFile)

            // زر الحفظ
            saveButton.setOnClickListener {
                onSaveClick(statusFile)
            }

            // النقر على الصورة للمعاينة
            itemView.setOnClickListener {
                // يمكن إضافة معاينة هنا
            }
        }
    }
}
