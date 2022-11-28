package com.example.team9

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_four.*
import kotlinx.android.synthetic.main.fragment_four.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class FragmentFour : Fragment() {
    // TODO: Rename and change types of parameters

    private var viewProfile : View? = null
    var pickImageFromAlbum =0
    var fbStorage : FirebaseStorage? = null
    var uriPhoto : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewProfile = inflater.inflate(R.layout.fragment_four,container,false)

        fbStorage = FirebaseStorage.getInstance()

        viewProfile!!.uploadButton.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
        }
        return viewProfile
    }


    // Inflate the layout for this fragment


    companion object {


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==pickImageFromAlbum){
            if(resultCode == Activity.RESULT_OK){
                //path for the selected image
                uriPhoto = data?.data
                myimage.setImageURI(uriPhoto)

                if(ContextCompat.checkSelfPermission(viewProfile!!.context,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    funImageUpload(viewProfile!!)
                }
            }
            else{

            }
        }
    }

    private fun funImageUpload(viewProfile: View) {
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imgFileName = "IMAGE_" + timeStamp + "_.png"
        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            Toast.makeText(view?.context, "image Upload", Toast.LENGTH_SHORT).show()
        }
    }

}