package com.example.diagnostic

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build

class FlashlightController(private val context: Context) {
    private var isTorchOn = false

    fun toggleFlashlight(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                for (id in cameraManager.cameraIdList) {
                    val characteristics = cameraManager.getCameraCharacteristics(id)
                    val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                    if (hasFlash) {
                        isTorchOn = !isTorchOn
                        cameraManager.setTorchMode(id, isTorchOn)
                        return isTorchOn
                    }
                }
            } catch (_: Exception) {
                return false
            }
        }
        return false
    }

    fun turnOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isTorchOn) {
            try {
                val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                for (id in cameraManager.cameraIdList) {
                    cameraManager.setTorchMode(id, false)
                }
                isTorchOn = false
            } catch (_: Exception) {}
        }
    }
}
