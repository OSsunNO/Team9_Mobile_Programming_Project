package com.example.team9

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
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

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



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

        var excelList = readExcelFileFromAssets()
        val db = FirebaseFirestore.getInstance().collection("cctvLocation")
        Log.d("ITM", "${excelList[0]}")

    }
    private val sensorListener: SensorEventListener = object : SensorEventListener {
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
            if (acceleration > 12) {
//                Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
               showDialog()


//                val intent2 = Intent(Intent.ACTION_SENDTO).apply{
//                    data = Uri.parse("tel:029706468")
//
//                }
//                intent2.putExtra("smsbody","message")


//                startActivity(intent2)

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
    private fun showDialog(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("로그인")

        val inflater: LayoutInflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_sensor,null))

        builder. setPositiveButton("Ok"){

                p0, p1-> val intent = Intent(Intent.ACTION_DIAL).apply{
            data = Uri.parse("tel:029706468")
        }
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent) }, 15000)
        }

        builder.setNegativeButton("Cancel"){
            dialog,p1->dialog.cancel()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    //여기서 부터 fragment코드임
    //fragment chaging function
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(frame.id, fragment).commit()
    }


    data class CCTVLocation(val num:String, val address:String,
                            val cameraNum:String, val latitude:String, val longitude:String)

    private fun readExcelFileFromAssets(): MutableList<CCTVLocation> {
        var itemList: MutableList<CCTVLocation> = mutableListOf();
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
            // MutableList 생성
            var items: MutableList<CCTVLocation> = mutableListOf()

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
                    var latitude = ""
                    var longitude =""
                    //열 반복문
                    while (cellIter.hasNext()) {
                        val myCell = cellIter.next() as HSSFCell
                        when{
                            colno === 0 -> num = myCell.toString()
                            colno === 1 -> address = myCell.toString()
                            colno === 2 -> cameraNum = myCell.toString()
                            colno === 3 -> latitude = myCell.toString()
                            colno === 4 -> longitude = myCell.toString()
                        }
                        colno++
                    }
                    //열을 Mutablelist에 추가
                    items.add(CCTVLocation(num,address,cameraNum,latitude,longitude))
                }
                rowno++
            }
            Log.e("checking", " items: " + items)
            itemList = items
        }
        catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
        return itemList
    }
}
