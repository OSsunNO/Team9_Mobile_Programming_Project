package com.example.team9

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.dialog_siren.*


class FragmentTwo : Fragment() {

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton


    private var mediaPlayer: MediaPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {return inflater.inflate(R.layout.fragment_two, container, false)    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼 초기화
        playButton = requireView().findViewById(R.id.playbutton)
        pauseButton = requireView().findViewById(R.id.pausebutton)

//        mediaPlayer = MediaPlayer.create(this, R.raw.police11)
//        mediaPlayer?.start()


        //오류 나는부분
        playButton.setOnClickListener {
            //여기서 sirendialog 클래스를 불러와서 그 내용을 실행
            showDialog()
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.police11)
            mediaPlayer?.start()
        }
        pauseButton.setOnClickListener {
            mediaPlayer?.pause()
        }


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialog(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("싸이렌")


        val inflater: LayoutInflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_siren,null))


        //주석 없애면 handler로 시간초 설정할 수 있어
        builder.setPositiveButton("예"){

                p0, p1-> mediaPlayer?.pause()
                }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.getWindow()?.setGravity(Gravity.BOTTOM);
        alertDialog.show()


    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentTwo()
    }
}