package edu.sswu.petopia

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val restaurantImage: ImageView = findViewById(R.id.restaurant_image)
        val backButton: ImageView = findViewById(R.id.back_button)
        val restaurantName: TextView = findViewById(R.id.restaurant_name)
        val restaurantMenu: TextView = findViewById(R.id.restaurant_menu)
        val restaurantAddress: TextView = findViewById(R.id.restaurant_address)
        val restaurantAddress22: TextView = findViewById(R.id.restaurant_address22)
        val restaurantHours: TextView = findViewById(R.id.restaurant_hours)
        val restaurantContact: TextView = findViewById(R.id.restaurant_contact)
        val restaurantDescription: TextView = findViewById(R.id.restaurant_description)

        // Intent로 데이터 받기
        val name = intent.getStringExtra("name")
        val menu = intent.getStringExtra("menu")
        val address = intent.getStringExtra("address")
        val address22 = intent.getStringExtra("address22")
        val hours = intent.getStringExtra("hours")
        val contact = intent.getStringExtra("contact")
        val description = intent.getStringExtra("description")
        val imageUrl = intent.getStringExtra("image_url")
        val latitude = intent.getDoubleExtra("latitude", 0.0) // 위도
        val longitude = intent.getDoubleExtra("longitude", 0.0) // 경도

        // 데이터 설정
        restaurantName.text = name
        restaurantMenu.text = menu
        restaurantAddress.text = address
        restaurantAddress22.text = address22
        restaurantHours.text = hours
        restaurantContact.text = contact
        restaurantDescription.text = description

        // 이미지 설정 (imageUrl 없으면 기본 이미지 sample_image 사용)
        restaurantImage.load(imageUrl) {
            error(R.drawable.sample_image) // 오류 시 기본 이미지
            placeholder(R.drawable.sample_image) // 로딩 중 기본 이미지
        }

        // 뒤로가기 버튼
        backButton.setOnClickListener { finish() }

        // 추가: 위도 및 경도 정보 로그 출력 (디버깅 용도)
        if (latitude != 0.0 && longitude != 0.0) {
            println("Restaurant Location: Latitude = $latitude, Longitude = $longitude")
        }
    }
}
