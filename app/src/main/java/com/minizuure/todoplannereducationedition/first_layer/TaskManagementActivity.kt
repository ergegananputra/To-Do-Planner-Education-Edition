package com.minizuure.todoplannereducationedition.first_layer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ActivityTaskManagementBinding

class TaskManagementActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTaskManagementBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}