package com.example.team9

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private val frame: FrameLayout by lazy { // activity_main의 화면 부분
        findViewById(R.id.fl_container)
    }

    private val bottomNagivationView: BottomNavigationView by lazy { // 하단 네비게이션 바
        findViewById(R.id.bottombar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // the mutable list which contains cctv data
        //var excelList = readExcelFileFromAssets()

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

    fun replaceFragment(fragment: Fragment){
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