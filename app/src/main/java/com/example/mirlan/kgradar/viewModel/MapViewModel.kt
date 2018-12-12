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

    private var policeLocationList: MutableLiveData<List<PoliceLocation>>? = null
    private var mDatabase: DatabaseReference? = null

    fun getPoliceLocations(): MutableLiveData<List<PoliceLocation>>? {
        if (policeLocationList == null) {
            policeLocationList = MutableLiveData<List<PoliceLocation>>()
            loadLocations()
           //
           // val myRef = database.getReference("Locations")
        }

        return policeLocationList
    }

    private fun loadLocations() {
        //val police = PoliceLocation.create()
        mDatabase = FirebaseDatabase.getInstance().reference.child("locations")

        val newloc = mDatabase!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (datasnapshot in p0.children){
                    //policeLocationList
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
    fun save(lat: Double, lng: Double, like: Int){

        mDatabase = FirebaseDatabase.getInstance().reference
        val newLocation = mDatabase!!.child(FIREBASE_PATH).push()
        val userLike = newLocation.child(FIREBASE_LIKE).push()//child(newLocation.key.toString()).push()
        val police = PoliceLocation.create()
        police.lat = lat
        police.lng = lng
        police.userId = newLocation.key
        newLocation.setValue(police)
        userLike.setValue(like)
    }


}