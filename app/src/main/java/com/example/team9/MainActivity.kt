package com.example.team9


import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.MapView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.android.synthetic.main.fragment_four.*
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


    private lateinit var SensorSwitch: ToggleButton
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private var sensorFlag = 1

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       /* try {
            //val fragment : Fragment클래스 = supportFragmentManager.findFragmentById(R.id.프래그먼트컨테이너) as Fragment클래스

            val fm = supportFragmentManager
//            fm.executePendingTransactions()
            val fragment : FragmentFour = fm.findFragmentById(R.layout.fragment_four) as FragmentFour
            SensorSwitch = fragment.requireView().findViewById(R.id.toggleButton)
            SensorSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    sensorFlag = 1
                } else {
                    sensorFlag = 0
                }
            }
        }
        catch(e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }*/

        // load the cctv data using roomDB
        // If the user launch the application first, he or she have to use this method for saving the cctv data into their RoomDB
//        readExcelFileFromAssets()

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



        // the code for uploading the cctv data to firestore
        /*val db = Firebase.firestore

        for (i in 1..excelList.size-1 ) {
            var cctv = hashMapOf(
                "num" to excelList[i].num,
                "address" to excelList[i].address,
                "cameraNum" to excelList[i].cameraNum,
                "latitude" to excelList[i].latitude,
                "longitude" to excelList[i].longitude
            )

            db.collection("cctvs").document("cctvInfo$i")
                .set(cctv)
                .addOnSuccessListener { Log.d("firestore", "Success!") }
                .addOnFailureListener { e-> Log.w("firestore", "Error", e) }
        }*/
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

//                Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
                //다이얼로그를 보여준다
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
            data = Uri.parse("tel:01066930480")
        }
        val intentgo = Intent(this,textActivity::class.java)

        val handler = Handler(Looper.getMainLooper())

        val handlerTask = object : Runnable {
            override fun run() {
                startActivity(intentgo)
                startActivity(intent)
            }
        }

        handler.postDelayed(handlerTask,15000)


        val inflater: LayoutInflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_sensor,null))

        //주석 없애면 handler로 시간초 설정할 수 있어
        builder.setPositiveButton("신고"){
                p0, p1->
            startActivity(intentgo)
            //주석 없애면 handler로 시간초 설정할 수 있어
            //위 코드는 delay 사용 아래는 미사용 둘 중 하나만 선택
//            requestPermission {startActivity(intent)}
            startActivity(intent)
            sensorFlag = 1
        }

        builder.setNeutralButton("닫기"){
                dialog,p1->   dialog.cancel()
            handler.removeCallbacks(handlerTask)
            sensorFlag = 1
        }

        builder.setNegativeButton("문자"){
                dialog,p1->
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
                    Toast.makeText( this@MainActivity,
                        "권한을 허용하지 않을 시 특정 기능 사용에 제한이 있을 수 있습니다.",
                        Toast.LENGTH_SHORT).show()
                }
            })
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS
                ,Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }


    //fragment chaging function
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(frame.id, fragment).commit()
    }


//    data class CCTVLocation(val num:String, val address:String,
//                            val cameraNum:String, val latitude:String, val longitude:String)

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
