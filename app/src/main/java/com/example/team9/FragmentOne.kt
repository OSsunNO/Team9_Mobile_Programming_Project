package com.example.team9

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.rpc.context.AttributeContext.Resource
import kotlinx.android.synthetic.main.fragment_one.view.*
import java.io.IOException
import java.lang.Math.*
import java.util.*
import javax.annotation.Nullable
import kotlin.math.pow


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

    private lateinit var mMap: MapView
    lateinit var mainActivity: MainActivity // context
    lateinit var permLauncher: ActivityResultLauncher<String>
    lateinit var locationStr: String

    private val r = 6372.8 * 1000
    var lat = 0.0
    var long = 0.0
    lateinit var currentMarker: Marker
    lateinit var cctvMarker: Marker
    val cctvDB: CCTVDB by lazy { CCTVDB.getInstance(requireContext()) } // initiating the database
//    private val mapIcon by lazy {
//        val drawable =
//            ResourcesCompat.getDrawable(resources, R.drawable.mapicon, null) as BitmapDrawable
//        Bitmap.createScaledBitmap(drawable.bitmap, 64, 64, false)
//    }
    private val mapIcon by lazy{
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.cctv, null) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 64, 64, false)
    }

    private val myIcon by lazy{
        val drawble2 = ResourcesCompat.getDrawable(resources, R.drawable.user, null) as BitmapDrawable
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
                lat = location!!.latitude//location!!.latitude
                long = location.longitude//location.longitude
                locationStr = location.toString()
                Toast.makeText(mainActivity, locationStr, Toast.LENGTH_LONG).show()
//        CameraPosition.builder().target(place).zoom(100.0f).build()
                Log.d("abc", "$lat, $long")
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
                Toast.makeText(mainActivity, locationStr, Toast.LENGTH_LONG).show()
//        CameraPosition.builder().target(place).zoom(100.0f).build()
                Log.d("abc", "$lat, $long")
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


//    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
//        val dLat = toRadians(lat2 - lat1)
//        val dLon = toRadians(lon2 - lon1)
//        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(toRadians(lat1)) * cos(toRadians(lat2))
//        val c = 2 * asin(sqrt(a))
//        return (r * c).toInt()
//    }

    // 내가 사용할 수 있는 Map이 GoogleMap 파라미터를 통해 전달
    @SuppressLint("SuspiciousIndentation","MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        // 파이어 스토어에서 intances get
        // val fireStore = FirebaseFirestore.getInstance()

        // 경도/위도 기반 위치 저장
        getLocationWithFine()
        val place = LatLng(lat, long)
        // room DB에서 instances get
        val cctvNear = cctvDB.cctvDAO().getNear(lat,long)
        Log.d("kk", "${cctvNear.size}")
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 17f))
        // 현재위치에 띄울 마커
        val markerOptions = MarkerOptions().position(place).title(getCurrentAddress(place)).icon(BitmapDescriptorFactory.fromBitmap(myIcon))
        currentMarker = googleMap.addMarker(markerOptions)!!
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
//        val aroundCCTV = cctvDB.cctvDAO().getAroundCCTV(lat, long)
//        for (i in 0..aroundCCTV.size) {
//            val marker = MarkerOptions().position(
//                LatLng(
//                    aroundCCTV[i].latitude,
//                    aroundCCTV[i].longitude
//                )
//            ).title(aroundCCTV[i].address).icon(BitmapDescriptorFactory.fromBitmap(mapIcon))
//
//            googleMap.addMarker(marker)
//        }
        gpsButton.setOnClickListener {
            getLocationWithFine()
            val place1 = LatLng(lat, long)
            val markerOptions1 = MarkerOptions().position(place1).title(getCurrentAddress(place1)).icon(BitmapDescriptorFactory.fromBitmap(myIcon))
            currentMarker.remove()
            cctvMarker.remove()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1, 17f))
            currentMarker = googleMap.addMarker(markerOptions1)!!
//        CameraPosition.builder().target(place).zoom(100.0f).build()
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
        // 구글맵에 띄울 마커 파이어스토어 ver
        /*for (i in 1..46163) {
            fireStore.collection("cctvs").document("cctvInfo$i")
                .get().addOnSuccessListener { result ->
                    val marker = MarkerOptions()
                        .position(
                            LatLng(
                                result.get("latitude").toString().toDouble(),
                                result.get("longitude").toString().toDouble()
                            )
                        )
                        .title(result.get("address").toString())
                    googleMap.addMarker(marker)
                }
        }*/
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



