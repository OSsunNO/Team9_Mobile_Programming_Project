package com.example.team9

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
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
    val cctvDB: CCTVDB by lazy { CCTVDB.getInstance(requireContext()) } // initiating the database
    lateinit var location: String
    lateinit var gpsButton: ImageButton
    lateinit var permLauncher: ActivityResultLauncher<String>

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
        gpsButton.setOnClickListener() {
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
    @SuppressLint("SuspiciousIndentation")
    override fun onMapReady(googleMap: GoogleMap) {

        // 파이어 스토어에서 intances get
        // val fireStore = FirebaseFirestore.getInstance()

        // room DB에서 instances get
        val cctvData = cctvDB.cctvDAO().getAll()
        // 경도/위도 기반 위치 저장
        val place = LatLng(37.57797241, 127.0930099)
//        CameraPosition.builder().target(place).zoom(100.0f).build()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 20f))
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

//    private fun getLastLocation() {
//(LocationManager)activity.getSystemService(LOCATION_SERVICE)
//        val locationManager = MainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
//        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString()
//        if (location != null) {
//            homeViewModel.setMyLocation(location.latitude, location.longitude)
//        }
//    }

//}
