package com.example.team9

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.team9.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.fragment_two.*

class SirenDialog(activity: Activity,): Dialog(activity) {

    private lateinit var okButton: Button

//    private var mediaPlayer: MediaPlayer?=null

    @SuppressLint("CheckResult", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_siren)

        val window = window



        if (window != null) {

            window.setBackgroundDrawableResource(R.color.blackDim_100)
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            val params = window.attributes

            //화면 가득 체움
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT

            //열기 & 닫기 시 애니메이션 설정
            window.attributes = params

        }

        initListener()

    }

    private fun initListener() {
        okButton = findViewById(R.id.btn_ok)

        okButton.setOnClickListener {
//            mediaPlayer?.pause()

            dismiss()
        }
    }
}