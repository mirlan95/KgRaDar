package com.example.mirlan.kgradar.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.mirlan.kgradar.data.Constants.FIREBASE_LIKE
import com.example.mirlan.kgradar.data.Constants.FIREBASE_PATH
import com.example.mirlan.kgradar.data.model.PoliceLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference

class MapViewModel : ViewModel() {

    private var policeLocationList: MutableLiveData<MutableList<PoliceLocation>>? = null
    private var mDatabase: DatabaseReference? = null

    fun getPoliceLocations(): MutableLiveData<MutableList<PoliceLocation>>? {
        if (policeLocationList == null) {
            policeLocationList = MutableLiveData<MutableList<PoliceLocation>>()
            loadLocations()
        }
        return policeLocationList
    }

    private fun loadLocations() {

        mDatabase = FirebaseDatabase.getInstance().reference.child("locations")
        val locationsListener = object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ///val location = dataSnapshot.getValue(PoliceLocation::class.java) ?: return
                var location:PoliceLocation?=null
                dataSnapshot.children.forEach {
                     location = it.getValue(PoliceLocation::class.java)
                }
                policeLocationList?.value?.add(location!!)

            }

        }
        mDatabase!!.addValueEventListener(locationsListener)

    }
    fun save(lat: Double, lng: Double, like: Int){

        mDatabase = FirebaseDatabase.getInstance().reference
        val newLocation = mDatabase!!.child(FIREBASE_PATH).push()
        val userLike = newLocation.child(FIREBASE_LIKE).push()//child(newLocation.key.toString()).push()
//        val police = PoliceLocation.create()
        val police =  PoliceLocation()
        police.lat = lat
        police.lng = lng
        police.userId = newLocation.key
        newLocation.setValue(police)
        userLike.setValue(like)
    }


}