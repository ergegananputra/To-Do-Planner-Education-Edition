package com.minizuure.todoplannereducationedition

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.minizuure.todoplannereducationedition.databinding.ActivityMainBinding
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModel
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.notification.AndroidAlarmManager
import com.minizuure.todoplannereducationedition.services.notification.CHANNEL_ID
import com.minizuure.todoplannereducationedition.services.notification.ItemAlarmQueue
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CustomSystemTweak(this)
            .statusBarTweak()
            .customNavigationBarColorSet(com.google.android.material.R.attr.colorSurfaceContainer)


        // Bottom Navigation
        with(binding) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_surface) as NavHostFragment
            val navController  = navHostFragment.navController
            bottomNavigationViewSurface.setupWithNavController(navController)
        }

        startNotificationService()
        optimizeSessionTaskProviderDatabase()
    }

    private fun optimizeSessionTaskProviderDatabase() {
        val app = application as ToDoPlannerApplication
        val factory = SessionTaskProviderViewModelFactory(app.appRepository)
        val sessionTaskProviderViewModel = ViewModelProvider(this, factory)[SessionTaskProviderViewModel::class.java]

        sessionTaskProviderViewModel.optimizeSessionTaskProviderTable()
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
        ).apply {
            setShowBadge(true)
            lightColor = getColor(R.color.white)
            enableLights(true)
            description = getString(R.string.notification_channel_main_description)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


        val app = application as ToDoPlannerApplication
        val alarmManager = app.appAlarmManager


        // TODO:  Delete this line in production
//        unitTesting(alarmManager)

    }

    /**
     * [unitTesting]
     *
     *
     * Make sure to change the [ItemAlarmQueue] object to your needs.
     * the taskId is the id of the task in the database.
     * the taskDateIdentification is the date of the Notes in ZoneDateTime accurate to the day.
     *
     *
     * @param alarmManager
     *
     */
    private fun unitTesting(alarmManager: AndroidAlarmManager) {
        val itemAlarmQueue = ItemAlarmQueue(
            1,
            CATEGORY_QUIZ,
            11,
            DatetimeAppManager(true).selectedDetailDatetimeISO.plusMinutes(1),
            "Mathematics",
            "Chapter 1: Algebra",
            DatetimeAppManager(true).selectedDetailDatetimeISO.plusWeeks(1)
        )

        lifecycleScope.launch {
            alarmManager.schedule(itemAlarmQueue)

        }
    }


}