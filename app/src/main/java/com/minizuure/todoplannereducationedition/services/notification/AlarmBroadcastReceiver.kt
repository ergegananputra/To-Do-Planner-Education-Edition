package com.minizuure.todoplannereducationedition.services.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO: [FS/P01] Reschedule all alarms
            println("AlarmBroadcastReceiver: BOOT_COMPLETED")
        } else {
            // TODO: [FS/P01] Show notification

            val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
            println("AlarmBroadcastReceiver: $message")
        }
    }
}