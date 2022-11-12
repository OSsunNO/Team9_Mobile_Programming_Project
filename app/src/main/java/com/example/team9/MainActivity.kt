package com.example.team9

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

class MainActivity : AppCompatActivity() {

//    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("ITM", "Hello")

        var excelList = readExcelFileFromAssets()
        val db = FirebaseFirestore.getInstance().collection("cctvLocation")
        Log.d("ITM", "${excelList[0]}")

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