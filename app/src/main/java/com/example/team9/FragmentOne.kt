package com.example.team9

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentOne.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentOne : Fragment(), OnMapReadyCallback {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    private lateinit var mMap: MapView
    lateinit var mainActivity: MainActivity // context
    lateinit var permLauncher: ActivityResultLauncher<String>
    lateinit var locationStr: String

    var lat = 0.0
    var long = 0.0
    lateinit var currentMarker: Marker
    lateinit var cctvMarker: Marker
    val cctvDB: CCTVDB by lazy { CCTVDB.getInstance(requireContext()) } // initiating the database

    private val mapIcon by lazy{
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.cctv, null) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 64, 64, false)
    }
    private val myIcon by lazy{
        val drawble2 = ResourcesCompat.getDrawable(resources, R.drawable.user1, null) as BitmapDrawable
        Bitmap.createScaledBitmap(drawble2.bitmap, 144, 144, false)
    }
    lateinit var gpsButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_one, container, false)
        mMap = rootView.findViewById(R.id.mapview) as MapView
        mMap.onCreate(savedInstanceState)
        mMap.getMapAsync(this)

        gpsButton = rootView.findViewById(R.id.GPSbutton)
        permLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.d(
                        "ITM",
                        "Now, permission granted by the user"
                    )
                } else {
                    Log.d("ITM", "permission request denied. next time, we need to explain WHY.")
                }
            }

        return rootView
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d("ITM", "onLocationResult()")
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            lat = locationResult.lastLocation!!.latitude
            long = locationResult.lastLocation!!.longitude
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentOne().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun getGpsState(): Boolean{
        var gpsEnable = false
        var manager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            gpsEnable = true
        }
        return gpsEnable
    }


    @SuppressLint("SuspiciousIndentation","MissingPermission")
    fun getLocationWithFine(){
        when {
            ContextCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                val locationManager =
                    mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
                val location =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location!=null) {
                    lat = location!!.latitude//location!!.latitude
                    long = location.longitude//location.longitude
                    locationStr = location.toString()
                }
                else{
                    if(getGpsState()){
                        if (mFusedLocationProviderClient == null) {
                            mFusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(mainActivity)
                        }
                        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                    }
                    else{
                        Toast.makeText(mainActivity,"GPS 혹은 네트워크 설정이 꺼져있습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            // first attempt일 때는 false 두 번째 시도부터 true
            shouldShowRequestPermissionRationale("android.permission.ACCESS_FINE_LOCATION") -> {
                Log.d("ITM", "without this? you cannot use our app :D")
            }
            else -> {
                Log.d("ITM", "request permission")
                permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

    }

    @SuppressLint("SuspiciousIndentation","MissingPermission")
    fun getLocationWithCoarse(){
        when {
            ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                val locationManager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                lat = location!!.latitude//location!!.latitude
                long = location.longitude//location.longitude
                locationStr = location.toString()
            }
            // first attempt일 때는 false 두 번째 시도부터 true
            shouldShowRequestPermissionRationale("android.permission.ACCESS_FINE_LOCATION") -> {
                Log.d("ITM", "without this? you cannot use our app :D")
            }
            else -> {
                Log.d("ITM", "request permission")
                permLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }


    fun getCurrentAddress(latlng: LatLng): String {
        // 위치 정보와 지역으로부터 주소 문자열을 구한다.
        var addressList: List<Address>? = null
        val geocoder =
            Geocoder(mainActivity, Locale.getDefault())

        // 지오코더를 이용하여 주소 리스트를 구한다.
        addressList =
            try {
                geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
            } catch (e: IOException) {
                Toast.makeText(
                    mainActivity,
                    "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
                return "주소 인식 불가"
            }
        if (addressList!!.size < 1) { // 주소 리스트가 비어있는지 비어 있으면
            return "해당 위치에 주소 없음"
        }
        return addressList[0].getAddressLine(0).toString()
    }
    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        Log.d("ITM", "startLocationUpdates()")

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mainActivity,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ITM", "startLocationUpdates() 두 위치 권한중 하나라도 없는 경우 ")
            return
        }
        Log.d("ITM", "startLocationUpdates() 위치 권한이 하나라도 존재하는 경우")
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청합니다.
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }



    // 내가 사용할 수 있는 Map이 GoogleMap 파라미터를 통해 전달
    @SuppressLint("SuspiciousIndentation","MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        mLocationRequest =  LocationRequest.create().apply {
            interval = 100 // 업데이트 간격 단위(밀리초)
            fastestInterval = 10 // 가장 빠른 업데이트 간격 단위(밀리초)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 정확성
            maxWaitTime= 100 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

        var Firestore: FirebaseFirestore? = null
        var auth: FirebaseAuth? = null
        Firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val myemail = FirebaseAuth.getInstance().currentUser!!.email.toString()


        if(getGpsState()){
            getLocationWithFine()
        }
        else{
            Toast.makeText(mainActivity,"GPS 혹은 네트워크 설정이 꺼져있습니다.",Toast.LENGTH_SHORT).show()
        }

        if(lat ==0.0 || long ==0.0) {
            Toast.makeText(
                mainActivity,
                "위치를 불러오는 데에 실패했습니다. 잠시 후 위치 갱신 버튼을 눌러 위치를 불러와주세요.",
                Toast.LENGTH_LONG
            ).show()
        }
        val place = LatLng(lat, long)
        // room DB에서 instances get
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 17f))
        val cctvNear = cctvDB.cctvDAO().getNear(lat,long)
//        Log.d("kk", "${cctvNear.size}")
        // 현재위치에 띄울 마커
        val markerOptions = MarkerOptions().position(place).title(getCurrentAddress(place)).icon(BitmapDescriptorFactory.fromBitmap(myIcon))
        currentMarker = googleMap.addMarker(markerOptions)!!

        var map = mutableMapOf<String,Any>()
        map["address"] = getCurrentAddress(place)
        Firestore.collection("$myemail")
            .document(auth.currentUser!!.email.toString()).update(map)
        // 구글맵에 띄울 마커 roomDB ver
        if (cctvNear.size!=0){
            for (i in 0..cctvNear.size-1) {
                val markerOptionsForCCTV = MarkerOptions().position(
                    LatLng(
                        cctvNear[i].latitude,
                        cctvNear[i].longitude
                    )
                ).title(cctvNear[i].address).icon(BitmapDescriptorFactory.fromBitmap(mapIcon))
                cctvMarker = googleMap.addMarker(markerOptionsForCCTV)!!
            }
        }

        gpsButton.setOnClickListener {
            if(getGpsState()){
                getLocationWithFine()
            }
            else{
                Toast.makeText(mainActivity,"GPS 혹은 네트워크 설정이 꺼져있습니다.",Toast.LENGTH_SHORT).show()
            }
            val place1 = LatLng(lat, long)
            val markerOptions1 = MarkerOptions().position(place1).title(getCurrentAddress(place1)).icon(BitmapDescriptorFactory.fromBitmap(myIcon))
            currentMarker.remove()
            if (cctvNear.size!=0) {
                cctvMarker.remove()
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1, 17f))
            currentMarker = googleMap.addMarker(markerOptions1)!!
            var map = mutableMapOf<String,Any>()
            map["address"] = getCurrentAddress(place1)
            Firestore.collection("$myemail")
                .document(auth.currentUser!!.email.toString()).update(map)
            val cctvNear1 = cctvDB.cctvDAO().getNear(lat,long)
            if (cctvNear1.size!=0){
                for (i in 0..cctvNear1.size-1) {
                    val markerOptionsForCCTV1 = MarkerOptions().position(
                        LatLng(
                            cctvNear1[i].latitude,
                            cctvNear1[i].longitude
                        )
                    ).title(cctvNear1[i].address).icon(BitmapDescriptorFactory.fromBitmap(mapIcon))
                    cctvMarker = googleMap.addMarker(markerOptionsForCCTV1)!!
                }
            }
            else {
                Toast.makeText(mainActivity, "근처에 존재하는 CCTV가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        mMap.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMap.onStop()
    }

    override fun onResume() {
        super.onResume()

        mMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap.onDestroy()
    }
}



