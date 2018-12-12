package com.example.mirlan.kgradar

class FirebaseGetData {
    object Holder{
        val value = synchronized(FirebaseGetData::class.java){ FirebaseGetData() }
    }
    companion object {
        val intance: FirebaseGetData by lazy { Holder.value }
    }
}