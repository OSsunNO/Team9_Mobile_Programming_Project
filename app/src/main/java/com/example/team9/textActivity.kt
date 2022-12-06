package com.example.team9

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class textActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        val db = Firebase.firestore
        val mydata = db.collection("nyk395@gmail.com").document("nyk395@gmail.com")
        mydata.addSnapshotListener { value, error ->
            if (value!=null&&value.exists()){
                val a =value.data!!["name"].toString()
                val b =value.data!!["age"].toString()
                val c =value.data!!["gender"].toString()
                val msg = "이름:$a+$b+세+성별:$c"
                val smsManager: SmsManager
                val policenumber = "114"

                if (Build.VERSION.SDK_INT>=23) {
                    //if SDK is greater that or equal to 23 then
                    //this is how we will initialize the SmsManager
                    smsManager = this.getSystemService(SmsManager::class.java)
                }
                else{
                    //if user's SDK is less than 23 then
                    //SmsManager will be initialized like this
                    smsManager = SmsManager.getDefault()
                }

                // on below line we are sending text message.

                smsManager.sendTextMessage(policenumber, null, msg, null, null)

            }
            val intentgomain = Intent(this,MainActivity::class.java)
            startActivity(intentgomain)
            finish()
        }
        //mydata.addSnapshotListener(EventListener<DocumentSnapshot>){snapshot,e->if(snapshot!=null &&snapshot)}


    }

}