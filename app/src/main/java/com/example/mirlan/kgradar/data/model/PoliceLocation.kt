package com.example.mirlan.kgradar.data.model

class PoliceLocation{

    companion object Factory{
        fun create(): PoliceLocation = PoliceLocation()
    }
    var id: Int = 0
    var lng: Double = 0.0
    var lat: Double = 0.0
    val status: Boolean = false
    var userId: String? = null
        // var stars: MutableMap<String, Int> = HashMap()

}