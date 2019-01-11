package com.example.mirlan.kgradar.data.model

data class PoliceLocation(
    var id: Int = 0,
    var lng: Double = 0.0,
    var lat: Double = 0.0,
    var status: Boolean = false,
    var userId: String? = "",
    var likes : MutableMap<String, Int> = HashMap()
)

/*

    var id: Int = 0
    var lng: Double = 0.0
    var lat: Double = 0.0
    val status: Boolean = false
    var userId: String? = null
        // var stars: MutableMap<String, Int> = HashMap()
*/
