package com.example.team9

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager

class textActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        val smsManager: SmsManager
        val policenumber = "01066930480"
        val msg = "안녕하세요"
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
        val intentgomain = Intent(this,MainActivity::class.java)
        startActivity(intentgomain)
        finish()
    }
}