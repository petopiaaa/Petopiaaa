package edu.sswu.petopia.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import edu.sswu.petopia.DetailActivity // DetailActivity 임포트 추가

data class Restaurants(
    val name: String = "",
    val address: String? = null,
    val category: String? = null,
    val contact: String? = null,
    val description: String? = null,
    val hours: String? = null,
    val menu: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

class NearbyFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var db: FirebaseFirestore
    private lateinit var infoPanel: LinearLayout
    private lateinit var infoName: TextView
    private lateinit var infoAddress: TextView
    private lateinit var infoCategory: TextView
    private val markers = mutableListOf<Marker>() // 생성된 마커 저장 리스트
    private var selectedMarker: Marker? = null // 선택된 마커 추적 변수

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

        // 패널 클릭 시 DetailActivity로 이동
        infoPanel.setOnClickListener {
            selectedMarker?.let { marker ->
                val restaurant = marker.tag as? Restaurants
                if (restaurant != null) {
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra("name", restaurant.name)
                        putExtra("menu", restaurant.menu)
                        putExtra("address", restaurant.address)
                        putExtra("category", restaurant.category)
                        putExtra("description", restaurant.description)
                        putExtra("hours", restaurant.hours)
                        putExtra("contact", restaurant.contact)
                        putExtra("latitude", restaurant.latitude)
                        putExtra("longitude", restaurant.longitude)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "식당 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 스크롤 가능한 필터 버튼 초기화
        val filterButtons = listOf(
            Pair(view.findViewById<Button>(R.id.filter_all), null),
            Pair(view.findViewById<Button>(R.id.filter_korean), "한식"),
            Pair(view.findViewById<Button>(R.id.filter_chinese), "중식"),
            Pair(view.findViewById<Button>(R.id.filter_japanese), "일식"),
            Pair(view.findViewById<Button>(R.id.filter_cafe), "카페"),
            Pair(view.findViewById<Button>(R.id.filter_western), "양식"),
            Pair(view.findViewById<Button>(R.id.filter_pub), "주점")
        )

        filterButtons.forEach { (button, category) ->
            button.setOnClickListener { filterMarkers(category) }
        }

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
                        addMarker(restaurant)
                    } catch (e: Exception) {
                        Log.e("FirestoreMappingError", "Error mapping document: ${document.id}", e)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "데이터를 가져오지 못했습니다: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addMarker(restaurant: Restaurants) {
        val marker = Marker()
        marker.position = LatLng(restaurant.latitude, restaurant.longitude)
        marker.map = naverMap
        marker.captionText = restaurant.name
        marker.tag = restaurant // 마커에 Restaurant 객체 저장

        marker.icon = when (restaurant.category) {
            "양식" -> OverlayImage.fromResource(R.drawable.ic_western_food)
            "카페" -> OverlayImage.fromResource(R.drawable.ic_cafe)
            "일식" -> OverlayImage.fromResource(R.drawable.ic_japanese_food)
            "한식" -> OverlayImage.fromResource(R.drawable.ic_korean_food)
            "중식" -> OverlayImage.fromResource(R.drawable.ic_chinese_food)
            "주점" -> OverlayImage.fromResource(R.drawable.ic_pub)
            else -> OverlayImage.fromResource(R.drawable.ic_default)
        }

        markers.add(marker)

        marker.setOnClickListener {
            marker.icon = OverlayImage.fromResource(R.drawable.ic_selected)
            selectedMarker = marker

            val cameraUpdate = CameraUpdate.scrollTo(marker.position)
            naverMap.moveCamera(cameraUpdate)

            infoPanel.visibility = View.VISIBLE
            infoName.text = restaurant.name
            infoAddress.text = restaurant.address ?: "주소 없음"
            infoCategory.text = restaurant.category ?: "카테고리 없음"
            true
        }
    }

    private fun filterMarkers(category: String?) {
        markers.forEach { it.map = null }
        markers.forEach { marker ->
            if (category == null || marker.tag == category) {
                marker.map = naverMap
            }
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
