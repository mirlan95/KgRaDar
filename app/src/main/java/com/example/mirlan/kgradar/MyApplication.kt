package com.example.mirlan.kgradar

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}