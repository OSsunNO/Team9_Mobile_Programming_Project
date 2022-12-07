package com.example.team9

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentManager
import com.example.team9.databinding.ActivityMainBinding
import com.example.team9.databinding.FragmentOneBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.MapFieldLite
import kotlinx.android.synthetic.main.fragment_one.view.*

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
    var lat = 0.0
    var long = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val cctvDB: CCTVDB by lazy { CCTVDB.getInstance(requireContext()) } // initiating the database
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

    // 내가 사용할 수 있는 Map이 GoogleMap 파라미터를 통해 전달
    @SuppressLint("SuspiciousIndentation","MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        // 파이어 스토어에서 intances get
        // val fireStore = FirebaseFirestore.getInstance()

        // room DB에서 instances get
        val cctvData = cctvDB.cctvDAO().getAll()
        // 경도/위도 기반 위치 저장
        when {
            ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                val locationManager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
//                var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
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

        val place = LatLng(lat, long)
//        CameraPosition.builder().target(place).zoom(100.0f).build()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 17f))
        // 구글맵에 띄울 마커 roomDB ver
        for (i in 1..100) {
            val marker = MarkerOptions().position(
                LatLng(
                    cctvData[i].latitude.toDouble(),
                    cctvData[i].longitude.toDouble()
                )
            ).title(cctvData[i].address)
            googleMap.addMarker(marker)
        }

        gpsButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    val locationManager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    lat = location!!.latitude
                    long = location.longitude
                    locationStr = location.toString()
                    Toast.makeText(mainActivity, locationStr, Toast.LENGTH_LONG).show()

                    var place1 = LatLng(lat, long)
//        CameraPosition.builder().target(place).zoom(100.0f).build()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1, 17f))

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
            when {
                ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    val locationManager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    lat = location!!.latitude
                    long = location.longitude
                    locationStr = location.toString()
                    Toast.makeText(mainActivity, locationStr, Toast.LENGTH_LONG).show()

                    val place2 = LatLng(lat, long)
//        CameraPosition.builder().target(place).zoom(100.0f).build()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place2, 17f))

                    Log.d("abc", "$lat, $long")
                    /*Log.d(
                        "ITM",
                        "${locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)}"
                    )*/
                }
                // first attempt일 때는 false 두 번째 시도부터 true
                shouldShowRequestPermissionRationale("android.permission.ACCESS_COARSE_LOCATION") -> {
                    Log.d("ITM", "without this? you cannot use our app :D")
                }
                else -> {
                    Log.d("ITM", "request permission")
                    permLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
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



