package com.example.team9

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class FragmentFour : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var imageView: ImageView
    private lateinit var uri: Uri
    private lateinit var btnLogout: Button
    private lateinit var gotoedit: Button
    private lateinit var myimage: ImageView

    private lateinit var nameText: TextView
    private lateinit var ageText: TextView
    private lateinit var genderText: TextView
    private lateinit var explanationText: TextView
    var googleSignInClient: GoogleSignInClient? = null

    private var storageReference = Firebase.storage
    var auth: FirebaseAuth? = null
    var Firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_four, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore
        val myemail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val mydata = db.collection("$myemail").document("$myemail")
        nameText = requireView().findViewById(R.id.NameText)
        ageText = requireView().findViewById(R.id.AgeText)
        genderText = requireView().findViewById(R.id.GenderText)
        explanationText = requireView().findViewById(R.id.explanationText)

        mydata.addSnapshotListener { value, error ->
            if (value != null && value.exists()) {
                val a = value.data!!["name"].toString()
                val b = value.data!!["age"].toString()
                val c = value.data!!["gender"].toString()
                val d = value.data!!["explanation"].toString()

                nameText.text = a
                ageText.text = b
                genderText.text = c
                explanationText.text =d
            }}

        myimage=requireView().findViewById(R.id.myimage)
        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://team9-ac4b9.appspot.com")
        val storageReference = storage.reference
        val myemailaddress = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val pathReference = storageReference.child("$myemailaddress/$myemailaddress")

        gotoedit = requireView().findViewById(R.id.gotoedit)

        gotoedit.setOnClickListener {
            val intentgoedit = Intent(activity, editActivity::class.java)
            startActivity(intentgoedit)
        }

        btnLogout = requireView().findViewById(R.id.logout)

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient?.signOut()
            var logoutIntent = Intent(context, LoginActivity::class.java)
            logoutIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(logoutIntent)
        }



        pathReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(myimage.rootView)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(myimage)

        }

    }

}




































