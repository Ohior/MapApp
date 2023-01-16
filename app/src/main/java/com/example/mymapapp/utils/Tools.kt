package com.example.mymapapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object Tools {
    fun displayPopup(activity: AppCompatActivity, message: String, dismissible: Boolean = true) {
        AlertDialog.Builder(activity).apply {
            setMessage(message)
            setCancelable(dismissible)
            create()
            show()
        }
    }

    fun isPermissionGranted(context: Context, stringPermission:String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            stringPermission
        ) == PackageManager.PERMISSION_GRANTED
    }
}