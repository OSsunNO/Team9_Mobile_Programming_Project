package com.example.team9

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.InputStream
import java.lang.Math.sqrt
import java.util.*

class MainActivity : AppCompatActivity() {

    private val frame:  FrameLayout by lazy { // activity_main의 화면 부분
        findViewById(R.id.fl_container)
    }

    private val bottomNagivationView: BottomNavigationView by lazy { // 하단 네비게이션 바
        findViewById(R.id.bottombar)
    }

    val cctvDB: CCTVDB by lazy {CCTVDB.getInstance(this)}

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var sensorFlag =1

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE)
        var checkFirst = pref.getBoolean("checkFirst", true)
        if (checkFirst == true){
            pref.edit().putBoolean("checkFirst",false).apply()
            readExcelFileFromAssets()
            Toast.makeText(this,"초기 설정 완료. 위치 갱신 버튼을 눌러 현재 위치를 불러와주세요.",Toast.LENGTH_LONG).show()
        }


        requestPermission{}

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Objects.requireNonNull(sensorManager)!!
            .registerListener(sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        // 애플리케이션 실행 후 첫 화면 설정
        supportFragmentManager.beginTransaction().add(frame.id,FragmentOne()).commit()

        // 하단 네비게이션 바 클릭 이벤트 설정
        bottomNagivationView.setOnItemSelectedListener{item ->
            when(item.itemId) {
                R.id.map -> {
                    replaceFragment(FragmentOne())
                    true
                }
                R.id.siren -> {
                    replaceFragment(FragmentTwo())
                    true
                }
                R.id.emergency -> {
                    replaceFragment(FragmentThree())
                    true
                }
                R.id.setting -> {
                    replaceFragment(FragmentFour())
                    true
                }

                else -> false
            }
        }

        Log.d("ITM", "Hello")
    }
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onSensorChanged(event: SensorEvent) {

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration>12&&sensorFlag==1){
                sensorFlag = 0
                showDialog()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }
    //function for showing dialog alert
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialog(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("신고하시겠습니까?")

        val intent = Intent(Intent.ACTION_CALL).apply{
            data = Uri.parse("tel:114")
        }
        val intentgo = Intent(this,textActivity::class.java)

        val handler = Handler(Looper.getMainLooper())

        val handlerTask = object : Runnable {
            override fun run() {
                intentgo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intentgo)
                finish()
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }

        handler.postDelayed(handlerTask,15000)


        val inflater: LayoutInflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_sensor,null))

        builder.setPositiveButton("신고"){
                p0, p1->
            intentgo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentgo)
            finish()
            handler.removeCallbacks(handlerTask)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            sensorFlag = 1
        }
        //주석 없애면 handler로 시간초 설정할 수 있어


        builder.setNeutralButton("닫기"){
                dialog,p1->   dialog.cancel()
            handler.removeCallbacks(handlerTask)
            sensorFlag = 1
        }


        val alertDialog: AlertDialog = builder.create()


        alertDialog.show()


    }

    //permission이 있는지 확인
    private fun requestPermission(logic : () -> Unit){
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }
                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Toast.makeText( context,
                        "권한을 허가해주세요.",
                        Toast.LENGTH_SHORT).show()
                }
            })
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    //여기서 부터 fragment코드임
    //fragment chaging function
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(frame.id, fragment).commit()
    }


    private fun readExcelFileFromAssets(): Unit {
        var itemList: MutableList<CCTV> = mutableListOf();
        try {
            val myInput: InputStream
            // assetManager 초기 설정
            val assetManager = assets
            //  엑셀 시트 열기
            myInput = assetManager.open("CCTV.xls")
            // POI File System 객체 만들기
            val myFileSystem = POIFSFileSystem(myInput)
            //워크 북
            val myWorkBook = HSSFWorkbook(myFileSystem)
            // 워크북에서 시트 가져오기
            val sheet = myWorkBook.getSheetAt(0)
            //행을 반복할 변수 만들어주기
            val rowIter = sheet.rowIterator()
            //행 넘버 변수 만들기
            var rowno = 0

            while (rowIter.hasNext()) {
                val myRow = rowIter.next() as HSSFRow
                if (rowno != 0) {
                    //열을 반복할 변수 만들어주기
                    val cellIter = myRow.cellIterator()
                    //열 넘버 변수 만들기
                    var colno = 0
                    var num = ""
                    var address = ""
                    var cameraNum = ""
                    var latitude = 0.0
                    var longitude = 0.0
                    //열 반복문
                    while (cellIter.hasNext()) {
                        val myCell = cellIter.next() as HSSFCell
                        when{
                            colno === 1 -> address = myCell.toString()
                            colno === 2 -> cameraNum = myCell.toString()
                            colno === 3 -> latitude = myCell.toString().toDouble()
                            colno === 4 -> longitude = myCell.toString().toDouble()
                        }
                        colno++
                    }
                    // CCTV data class양식에 맞는 Entity 생성
                    val item = CCTV(rowno,address,cameraNum,latitude,longitude)
                    // Entity를 cctvDB에 삽입
                    cctvDB.cctvDAO().insert(item)
                }
                rowno++
            }
        }
        catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}

