package com.minizuure.todoplannereducationedition

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.minizuure.todoplannereducationedition.databinding.ActivityMainBinding
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.notification.AndroidAlarmManager
import com.minizuure.todoplannereducationedition.services.notification.CHANNEL_ID
import com.minizuure.todoplannereducationedition.services.notification.ItemAlarmQueue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CustomSystemTweak(this).statusBarTweak()

        // Bottom Navigation
        with(binding) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_surface) as NavHostFragment
            val navController  = navHostFragment.navController
            bottomNavigationViewSurface.setupWithNavController(navController)
        }

        startNotificationService()
    }

    /**
     * [startNotificationService]
     *
     *
     */
    private fun startNotificationService() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_main_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.notification_channel_main_description)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)



        val alarmManager = AndroidAlarmManager(this)

        val itemAlarmQueue = ItemAlarmQueue(
            1,
            CATEGORY_QUIZ,
            DatetimeAppManager().selectedDetailDatetimeISO.plusMinutes(1),
            "Mathematics",
            "Chapter 1: Algebra",
            1
        )

        lifecycleScope.launch {
            alarmManager.schedule(itemAlarmQueue)
            delay(100)
            alarmManager.schedule(ItemAlarmQueue(
                2,
                CATEGORY_QUIZ,
                DatetimeAppManager().selectedDetailDatetimeISO.plusMinutes(2),
                "Physics",
                "Chapter 2: Geometry",
                1
            ))
            delay(300)
            alarmManager.cancel(itemAlarmQueue)

        }


    }


}