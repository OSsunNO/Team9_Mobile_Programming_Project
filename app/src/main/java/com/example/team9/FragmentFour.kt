package com.example.team9

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_four.*
import kotlinx.android.synthetic.main.fragment_four.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class FragmentFour : Fragment() {
    // TODO: Rename and change types of parameters

//    private var viewProfile : View? = null
//    var pickImageFromAlbum =0
//    var fbStorage : FirebaseStorage? = null
//    var uriPhoto : Uri? = null
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        viewProfile = inflater.inflate(R.layout.fragment_four,container,false)
//
//        fbStorage = FirebaseStorage.getInstance()
//
//        viewProfile!!.uploadButton.setOnClickListener {
//            var photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            imagePickerActivityResult.launch(photoPickerIntent)
//            //startActivityForResult(photoPickerIntent, pickImageFromAlbum)
//        }
//        return viewProfile
//    }
//    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
//    // lambda expression to receive a result back, here we
//        // receive single item(photo) on selection
//        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result != null) {
//                // getting URI of selected Image
//                val imageUri: Uri? = result.data?.data
//
//                // val fileName = imageUri?.pathSegments?.last()
//
//                // extract the file name with extension
//                val sd = getFileName(applicationContext, imageUri!!)
//
//                // Upload Task with upload to directory 'file'
//                // and name of the file remains same
//                val uploadTask = storageRef.child("file/$sd").putFile(imageUri)
//
//                // On success, download the file URL and display it
//                uploadTask.addOnSuccessListener {
//                    // using glide library to display the image
//                    storageRef.child("upload/$sd").downloadUrl.addOnSuccessListener {
//                        Glide.with(this@FragmentFour)
//                            .load(it)
//                            .into(myimage)
//
//                        Log.e("Firebase", "download passed")
//                    }.addOnFailureListener {
//                        Log.e("Firebase", "Failed in downloading")
//                    }
//                }.addOnFailureListener {
//                    Log.e("Firebase", "Image Upload fail")
//                }
//            }
//        }
//
//    @SuppressLint("Range")
//    private fun getFileName(context: Context, uri: Uri): String? {
//        if (uri.scheme == "content") {
//            val cursor = context.contentResolver.query(uri, null, null, null, null)
//            cursor.use {
//                if (cursor != null) {
//                    if(cursor.moveToFirst()) {
//                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                    }
//                }
//            }
//        }
//        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//    if(requestCode==pickImageFromAlbum){
//        if(resultCode == Activity.RESULT_OK){
//            //path for the selected image
//            uriPhoto = data?.data
//            myimage.setImageURI(uriPhoto)
//
//            if(ContextCompat.checkSelfPermission(viewProfile!!.context,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
//                funImageUpload(viewProfile!!)
//                }
//        }
//        else{
//
//        }
//    }
//    }

//    private fun funImageUpload(viewProfile: View) {
//        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        var imgFileName = "IMAGE_" + timeStamp + "_.png"
//        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)
//
//        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
//            Toast.makeText(view?.context, "image Upload", Toast.LENGTH_SHORT).show()
//        }
//    }

}
