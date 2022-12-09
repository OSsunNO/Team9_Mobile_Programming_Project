package com.example.team9

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.android.synthetic.main.fragment_three.*

class FragmentThree : Fragment() {

    private lateinit var emegencyButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emegencyButton = requireView().findViewById(R.id.emergencybutton)
        emegencyButton.setOnClickListener {
            showDialog()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_three, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialog(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("긴급상황")

        val inflater: LayoutInflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_emergency,null))

        builder.setPositiveButton("예"){

                p0, p1-> activity?.let{

            val iT = Intent(context, textActivity::class.java)
            iT.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(iT)

            }
            activity?.let{
                val intent = Intent(Intent.ACTION_CALL).apply{
                    data = Uri.parse("tel:114")

                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

        }
        builder.setNegativeButton("아니오"){
            p0,p1->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.getWindow()?.setGravity(Gravity.BOTTOM);
        alertDialog.show()

    }
}
