package com.example.team9

import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.fragment_four.*
import org.w3c.dom.Text


class FragmentFour : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var imageView: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnUpload: Button
    private lateinit var uri: Uri
    private lateinit var btnLogout: Button
    private lateinit var button: Button

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
        mainActivity = context as MainActivity
        btnLogout = requireView().findViewById(R.id.logout)

        imageView = requireView().findViewById(R.id.myimage)
        btnGallery = requireView().findViewById(R.id.gallery)
        btnUpload = requireView().findViewById(R.id.upload)
        button = requireView().findViewById(R.id.button)

        storageReference = FirebaseStorage.getInstance()

//        if(FirebaseAuth.getInstance().currentUser!!.uid==){
//            imageView.setImageBitmap()
//        }
        auth = FirebaseAuth.getInstance()
        Firestore = FirebaseFirestore.getInstance()

        val myname = "power"
        val myage= 32
        val mygender= editGender.text.toString()



            btnLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                googleSignInClient?.signOut()
                var logoutIntent = Intent(context, LoginActivity::class.java)
                logoutIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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
                textView3.text = editName.toString()


            }
            btnUpload.setOnClickListener {
                storageReference.getReference(FirebaseAuth.getInstance().currentUser!!.email.toString())
                    .child(FirebaseAuth.getInstance().currentUser!!.email.toString())
                    .putFile(uri)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { uri ->
                                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                                val imageMap = mapOf("url" to uri.toString())
                                val databaseReference =
                                    FirebaseDatabase.getInstance().getReference("userImages")
                                databaseReference.child(uid).setValue(imageMap)
                                    .addOnSuccessListener {

                                        Toast.makeText(context, "이거지", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { Log.d("ITM", "실패") }

                            }
                    }
            }
     val email = auth?.currentUser!!.email
        Log.d("ITM","$email+실험")
        button.setOnClickListener {
            var userInfomation = userInfo()

            userInfomation.name = editName.text.toString()
            userInfomation.age = editAge.text.toString()
            userInfomation.gender = editGender.text.toString()
            Firestore?.collection("$email")?.document(auth?.currentUser!!.email.toString())?.set(userInfomation)

        }
        }
    }


































