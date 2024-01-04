package com.minizuure.todoplannereducationedition.second_layer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minizuure.todoplannereducationedition.CustomSystemTweak
import com.minizuure.todoplannereducationedition.databinding.ActivityRoutineManagementBinding

class RoutineManagementActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRoutineManagementBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        CustomSystemTweak(this).statusBarTweak()

    }


}