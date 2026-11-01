package com.expensetracker.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.expensetracker.data.entity.TransactionEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    /**
     * Exports the given transactions to a CSV file in the public Downloads directory.
     * Returns the file name on success, or throws on failure.
     */
    fun export(
        context: Context,
        transactions: List<TransactionEntity>,
        startDate: Long,
        endDate: Long
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startStr = dateFormat.format(Date(startDate))
        val endStr = dateFormat.format(Date(endDate))
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "expenses_${startStr}_to_${endStr}_$timestamp.csv"

        val csvContent = buildString {
            appendLine("Date,Type,Category,Title,Amount")
            for (t in transactions) {
                val date = dateFormat.format(Date(t.dateMillis))
                val title = t.title.replace("\"", "\"\"")
                val category = t.categoryName.replace("\"", "\"\"")
                appendLine("$date,${t.type},\"$category\",\"$title\",${t.amount}")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore for Android 10+
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw RuntimeException("Failed to create file via MediaStore")
            resolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(csvContent)
                }
            } ?: throw RuntimeException("Failed to open output stream")
        } else {
            // Direct file access for Android 9 and below
            val downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { writer ->
                    writer.write(csvContent)
                }
            }
        }

        return fileName
    }
}
