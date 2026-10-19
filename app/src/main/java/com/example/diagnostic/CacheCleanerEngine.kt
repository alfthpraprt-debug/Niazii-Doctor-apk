package com.example.diagnostic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import com.example.data.model.CacheCleanupRecord
import java.io.File

class CacheCleanerEngine(private val context: Context) {

    fun getAppInternalCacheSize(): Long {
        var total = 0L
        context.cacheDir?.let { total += getFolderSize(it) }
        context.externalCacheDir?.let { total += getFolderSize(it) }
        context.codeCacheDir?.let { total += getFolderSize(it) }
        return total
    }

    private fun getFolderSize(file: File): Long {
        if (!file.exists()) return 0L
        if (file.isFile) return file.length()
        var size = 0L
        val children = file.listFiles() ?: return 0L
        for (child in children) {
            size += getFolderSize(child)
        }
        return size
    }

    /**
     * Mode A: Automatic Safe Cleanup
     * Safely cleans Niazi Doctor's own eligible temporary and cached files.
     * Complies 100% with modern Android security boundaries.
     */
    fun performModeACleanup(): CacheCleanupRecord {
        val freeBefore = getAvailableInternalStorageBytes()
        var bytesDeleted = 0L

        context.cacheDir?.let { dir ->
            bytesDeleted += deleteRecursive(dir, deleteSelf = false)
        }

        context.externalCacheDir?.let { dir ->
            bytesDeleted += deleteRecursive(dir, deleteSelf = false)
        }

        val freeAfter = getAvailableInternalStorageBytes()

        val notes = if (bytesDeleted > 0) {
            "نیازی ڈاکٹر کی عارضی اور کیش فائلیں کامیابی سے صاف ہو گئیں۔ ${formatBytes(bytesDeleted)} جگہ خالی ہوئی۔"
        } else {
            "نیازی ڈاکٹر کا کیش پہلے ہی بالکل صاف اور آپٹمائز تھا۔ کوئی اضافی عارضی فائل نہیں ملی۔"
        }

        return CacheCleanupRecord(
            timestamp = System.currentTimeMillis(),
            mode = "Mode A — Automatic Safe Cleanup",
            bytesCleaned = bytesDeleted,
            freeSpaceBefore = freeBefore,
            freeSpaceAfter = freeAfter,
            success = true,
            notesUrdu = notes
        )
    }

    private fun deleteRecursive(file: File, deleteSelf: Boolean): Long {
        var deletedBytes = 0L
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deletedBytes += deleteRecursive(child, deleteSelf = true)
            }
        }
        val len = file.length()
        if (deleteSelf) {
            if (file.delete()) {
                deletedBytes += len
            }
        }
        return deletedBytes
    }

    private fun getAvailableInternalStorageBytes(): Long {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (_: Exception) {
            0L
        }
    }

    /**
     * Mode B: Open system app details screen to let user clear cache safely
     */
    fun createOpenAppSettingsIntent(packageName: String): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Mode B: Open Android system storage settings
     */
    fun createOpenSystemStorageIntent(): Intent {
        val storageIntent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return if (storageIntent.resolveActivity(context.packageManager) != null) {
            storageIntent
        } else {
            Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    fun formatBytes(bytes: Long): String {
        val mb = bytes.toDouble() / (1024.0 * 1024.0)
        return if (mb >= 1024.0) {
            String.format(java.util.Locale.US, "%.2f GB", mb / 1024.0)
        } else {
            String.format(java.util.Locale.US, "%.1f MB", mb)
        }
    }
}
