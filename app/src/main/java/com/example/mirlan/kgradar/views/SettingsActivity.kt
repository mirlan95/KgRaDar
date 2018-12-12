package com.example.mirlan.kgradar.views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.mirlan.kgradar.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
