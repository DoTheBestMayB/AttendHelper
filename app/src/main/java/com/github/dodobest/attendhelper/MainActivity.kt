package com.github.dodobest.attendhelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.github.dodobest.attendhelper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainContract.View {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webViewButton.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }
    }
}