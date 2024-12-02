package edu.sswu.petopia.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import edu.sswu.petopia.R
import android.util.Log
import com.google.firebase.firestore.IgnoreExtraProperties


// Firestore 데이터 모델 정의
data class Restaurants(
    val name: String = "",
    val address: String? = null,
    val category: String? = null,
    val contact: String? = null,
    val description: String? = null,
    val hours: String? = null,
    val menu: String? = null,
    val latitude: Double = 0.0, // nullable로 변경
    val longitude: Double = 0.0 // nullable로 변경
)



class NearbyFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var db: FirebaseFirestore
    private lateinit var infoPanel: LinearLayout
    private lateinit var infoName: TextView
    private lateinit var infoAddress: TextView
    private lateinit var infoCategory: TextView

    // 위치 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            Toast.makeText(context, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoPanel = view.findViewById(R.id.info_panel)
        infoName = view.findViewById(R.id.info_name)
        infoAddress = view.findViewById(R.id.info_address)
        infoCategory = view.findViewById(R.id.info_category)

        if (!hasLocationPermission()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naver_map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naver_map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true

        fetchRestaurantsAndAddMarkers()

        if (hasLocationPermission()) {
            naverMap.addOnLocationChangeListener { location ->
                val currentLocation = LatLng(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdate.scrollTo(currentLocation)
                naverMap.moveCamera(cameraUpdate)
            }
        }
    }

    private fun fetchRestaurantsAndAddMarkers() {
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    try {
                        val restaurant = document.toObject(Restaurants::class.java)
                        if (restaurant.latitude != null && restaurant.longitude != null) {
                            addMarker(restaurant)
                        } else {
                            Log.e("FirestoreMappingError", "Latitude or Longitude is null for document: ${document.id}")
                        }
                    } catch (e: Exception) {
                        Log.e("FirestoreMappingError", "Error mapping document: ${document.id}", e)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "데이터를 가져오지 못했습니다: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun addMarker(restaurants: Restaurants) {
        if (restaurants.latitude != null && restaurants.longitude != null) {
            val marker = Marker()
            marker.position = LatLng(restaurants.latitude, restaurants.longitude)
            marker.map = naverMap
            marker.captionText = restaurants.name

            // 카테고리에 따른 아이콘 설정
            marker.icon = when (restaurants.category) {
                "양식" -> OverlayImage.fromResource(R.drawable.ic_western_food)
                "카페" -> OverlayImage.fromResource(R.drawable.ic_cafe)
                "일식" -> OverlayImage.fromResource(R.drawable.ic_japanese_food)
                "한식" -> OverlayImage.fromResource(R.drawable.ic_korean_food)
                "중식" -> OverlayImage.fromResource(R.drawable.ic_chinese_food)
                "주점" -> OverlayImage.fromResource(R.drawable.ic_pub)
                else -> OverlayImage.fromResource(R.drawable.ic_default)
            }

            // 마커 클릭 이벤트
            marker.setOnClickListener {
                infoPanel.visibility = View.VISIBLE
                infoName.text = restaurants.name
                infoAddress.text = restaurants.address ?: "주소 없음"
                infoCategory.text = restaurants.category ?: "카테고리 없음"
                true
            }
        } else {
            Log.e("addMarkerError", "Latitude or Longitude is null for restaurant: ${restaurants.name}")
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
