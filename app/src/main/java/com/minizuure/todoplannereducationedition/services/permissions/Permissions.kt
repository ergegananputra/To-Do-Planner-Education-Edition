package com.minizuure.todoplannereducationedition.services.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minizuure.todoplannereducationedition.R

class Permissions(
    private val context: Context,
    private val activity: AppCompatActivity = context as AppCompatActivity
) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val POST_NOTIFICATIONS_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    private var POST_NOTIFICATIONS_GRANTED = false

    @RequiresApi(Build.VERSION_CODES.S)
    private val SCHEDULE_EXACT_ALARM_PERMISSION = Manifest.permission.SCHEDULE_EXACT_ALARM
    private var SCHEDULE_EXACT_ALARM_GRANTED = false


    private val launcherPermissionAlarmRequest = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SCHEDULE_EXACT_ALARM_GRANTED = true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SCHEDULE_EXACT_ALARM_GRANTED = false
            showPermissionDialog("Schedule Alarm")
        }
    }

    private val launcherPermissionNotificationRequest = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            POST_NOTIFICATIONS_GRANTED = true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            POST_NOTIFICATIONS_GRANTED = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermissionAlarm() {
        if (ContextCompat.checkSelfPermission(context, SCHEDULE_EXACT_ALARM_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            SCHEDULE_EXACT_ALARM_GRANTED = true
        } else {
            launcherPermissionAlarmRequest.launch(SCHEDULE_EXACT_ALARM_PERMISSION)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissionNotification() : Boolean {
        return if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            POST_NOTIFICATIONS_GRANTED = true
            true
        } else {
            launcherPermissionNotificationRequest.launch(POST_NOTIFICATIONS_PERMISSION)
            POST_NOTIFICATIONS_GRANTED
        }
    }



    @RequiresApi(Build.VERSION_CODES.S)
    private fun showPermissionDialog(permissionDesc: String) {
        MaterialAlertDialogBuilder(context)
            .setIcon(R.drawable.ic_settings_outline)
            .setTitle("Application Setting")
            .setMessage("Permission $permissionDesc is required to use this feature")
            .setPositiveButton("Go To System Settings") { dialog, _ ->
                startActivity(context, Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM), null)
                dialog.dismiss()
            }
            .setNegativeButton("Exit") { dialog, _ ->
                SCHEDULE_EXACT_ALARM_GRANTED = true
                dialog.dismiss()
            }
            .create()
            .show()
    }
}