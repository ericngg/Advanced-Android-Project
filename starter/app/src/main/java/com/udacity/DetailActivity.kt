package com.udacity

import android.graphics.Color
import android.graphics.Color.green
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ContentDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        binding.tvFile.text = intent.getStringExtra("file")

        if (intent.getBooleanExtra("success", false)) {
            binding.tvSuccess.text = "Success"
            binding.tvSuccess.setTextColor(getColor(R.color.colorPrimaryDark))
        } else {
            binding.tvSuccess.text = "Failed"
            binding.tvSuccess.setTextColor(getColor(R.color.red))
        }

        binding.btnOk.setOnClickListener {
            finish()
        }
    }

}
