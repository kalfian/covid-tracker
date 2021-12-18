package com.ppb2.kalfian.covidtracker.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.ppb2.kalfian.covidtracker.models.CheckInHistory
import com.ppb2.kalfian.covidtracker.models.Place
import java.util.*
import kotlin.collections.HashMap

class DB {
    companion object {
        fun getPlaceTotal(db: DatabaseReference, place: Place, callback: (isError: Boolean, places: ArrayList<String>) -> Unit) {
            db.child("Places").child(place.id).child("current_entry").get().addOnSuccessListener {
                var data = arrayListOf<String>()

                it.children.forEach { c ->
                    val d = c.getValue<String>()
                    if (d != null) {
                        data.add(d)
                    }
                }

                callback(false, data)
            }.addOnFailureListener {
                callback(true, arrayListOf())
            }
        }

        fun getPlaceById(db: DatabaseReference, placeId: String, callback: (place: Place?) -> Unit) {
            db.child("Places").child(placeId).get().addOnSuccessListener {
                val place = it.getValue(Place::class.java)
                callback(place)
            }.addOnFailureListener {
                callback(null)
            }
        }

        fun checkIn(db: DatabaseReference, place: Place, uid: String, callback: (isError: Boolean, message: String) -> Unit) {
            // Insert to user Check In
            val checkInUid = db.child("UserCheckins").child(uid).push().key
            if(checkInUid == null) {
                callback(true, "Couldn't get push key for check in")
                return
            }

            val checkIn = CheckInHistory()
            checkIn.id = checkInUid
            checkIn.place_id = place.id
            checkIn.place_name = place.name
            checkIn.user_id = uid
            checkIn.user_place = "${uid}_${place.id}"

            val currentTimestamp =  System.currentTimeMillis() / 1000L
            checkIn.checkin_timestamp = currentTimestamp.toDouble()
            checkIn.checkout_timestamp = 0.0

            db.child("UserCheckins").child(uid).child(checkInUid).setValue(checkIn).addOnFailureListener {
                callback(true, it.localizedMessage.toString())
                return@addOnFailureListener
            }

            // Insert to Place current entry
            db.child("Places").child(place.id).child("current_entry").push().setValue(uid).addOnFailureListener {
                callback(true, it.localizedMessage.toString())
                return@addOnFailureListener
            }

            callback(false, "Success check in")
        }

        fun checkOut(db: DatabaseReference, uid: String, checkInHistory: CheckInHistory, callback: (isError: Boolean, message: String) -> Unit) {
            val updates: MutableMap<String, Any> = HashMap()

            val currentTimestamp =  System.currentTimeMillis() / 1000L
            updates["UserCheckins/$uid/${checkInHistory.id}/checkout_timestamp"] = currentTimestamp.toDouble()

            db.updateChildren(updates).addOnFailureListener {
                callback(true, it.localizedMessage.toString())
                return@addOnFailureListener
            }

            val emailListener = db.child("Places").child(checkInHistory.place_id).child("current_entry").orderByValue().equalTo(uid)
            emailListener.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (sn in snapshot.children) {
                        sn.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            callback(false, "Success update")
        }
    }
}