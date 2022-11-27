package com.example.team9

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.dialog_siren.*


class FragmentTwo : Fragment() {

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton

//private lateinit var btn_ok: Button

    private var mediaPlayer: MediaPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {return inflater.inflate(R.layout.fragment_two, container, false)    }

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
            SirenDialog(requireActivity()).show()
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.police11)
            mediaPlayer?.start()
        }
        pauseButton.setOnClickListener {
            mediaPlayer?.pause()
        }

//         btn_ok.setOnClickListener {
//            mediaPlayer?.pause()
//        }



    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentTwo()
    }
}