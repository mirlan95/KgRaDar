package com.example.mirlan.kgradar.views

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.mirlan.kgradar.R

class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
