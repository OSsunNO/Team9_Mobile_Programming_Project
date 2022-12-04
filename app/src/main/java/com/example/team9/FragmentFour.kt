package com.example.team9

import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_four.*


class FragmentFour : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var imageView: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnUpload: Button
    private lateinit var uri: Uri
    private lateinit var btnLogout: Button
    private lateinit var SensorSwitch: ToggleButton
    private var sensorManager: SensorManager? = null
    var googleSignInClient: GoogleSignInClient? = null

    private var storageReference = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = context as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_four, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SensorSwitch = requireView().findViewById(R.id.toggleButton)
        btnLogout = requireView().findViewById(R.id.logout)

        imageView = requireView().findViewById(R.id.myimage)
        btnGallery = requireView().findViewById(R.id.gallery)
        btnUpload = requireView().findViewById(R.id.upload)
        storageReference = FirebaseStorage.getInstance()



        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient?.signOut()
            var logoutIntent = Intent(context, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(logoutIntent)


        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                imageView.setImageURI(it)
                if (it != null) {
                    uri = it
                }

            })
        btnGallery.setOnClickListener {
            galleryImage.launch("image/*")
        }
        btnUpload.setOnClickListener {
            storageReference.getReference("Images").child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            val imageMap = mapOf("url" to uri.toString())
                            val databaseReference =
                                FirebaseDatabase.getInstance().getReference("userImages")
                            databaseReference.child(uid).setValue(imageMap).addOnSuccessListener {

                                Toast.makeText(context, "이거지", Toast.LENGTH_SHORT).show()
                            }
                                .addOnFailureListener { Log.d("ITM", "실패") }

                        }
                }
        }

    }
}
































