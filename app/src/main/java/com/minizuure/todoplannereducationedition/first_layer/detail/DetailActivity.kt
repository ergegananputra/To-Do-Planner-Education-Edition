package com.minizuure.todoplannereducationedition.first_layer.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.color.MaterialColors
import com.minizuure.todoplannereducationedition.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.appBarLayoutDetail.setStatusBarForegroundColor(
            MaterialColors.getColor(binding.appBarLayoutDetail,
                com.google.android.material.R.attr.colorSurface)
        )

        binding.toolbarDetail.setNavigationOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}