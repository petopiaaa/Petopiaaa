package edu.sswu.petopia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()

    val restaurants = MutableLiveData<List<Restaurant>>() // 전체 데이터
    val filteredRestaurants = MutableLiveData<List<Restaurant>>() // 필터링된 데이터

    fun fetchRestaurants(onComplete: () -> Unit) {
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { document ->
                    Restaurant(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        category = document.getString("category") ?: "",
                        location = document.getString("location") ?: "",
                        description = document.getString("description") ?: "",
                        hours = document.getString("hours") ?: "",
                        address = document.getString("address") ?: "",
                        address22 = document.getString("address22") ?: "",
                        menu = document.getString("menu") ?: "",
                        contact = document.getString("contact") ?: "",
                        imageUrl = document.getString("image_url") ?: ""
                    )
                }
                restaurants.value = list
                filteredRestaurants.value = list // 초기값은 전체 데이터
                onComplete()
            }
    }

    fun filterByCategory(category: String) {
        val data = restaurants.value ?: return
        filteredRestaurants.value = data.filter { it.category == category }
    }

    fun filterByRegion(regions: List<String>) {
        val data = restaurants.value ?: return
        if (regions.isEmpty()) {
            // 선택된 지역이 없으면 전체 데이터를 반환
            filteredRestaurants.value = data
        } else {
            // 선택된 지역이 있을 경우 해당 지역만 필터링
            filteredRestaurants.value = data.filter { it.location in regions }
        }
    }

    fun filterByKeyword(keyword: String) {
        val data = restaurants.value ?: return
        filteredRestaurants.value = data.filter { it.name.contains(keyword, ignoreCase = true) }
    }

    fun showAll() {
        val data = restaurants.value ?: return
        filteredRestaurants.value = data // 전체 데이터를 설정
    }

    // 추가된 메서드
    fun filterByKeywordAndRegion(keyword: String, regions: List<String>) {
        val data = restaurants.value ?: return
        filteredRestaurants.value = data.filter { restaurant ->
            // 키워드와 지역 필터 동시 적용
            restaurant.name.contains(keyword, ignoreCase = true) &&
                    (regions.isEmpty() || restaurant.location in regions)
        }
    }

    fun filterByCategoryAndRegion(category: String, regions: List<String>) {
        val data = restaurants.value ?: return
        filteredRestaurants.value = data.filter { restaurant ->
            // 카테고리와 지역 필터 동시 적용
            restaurant.category == category &&
                    (regions.isEmpty() || restaurant.location in regions)
        }
    }

    fun filterByAllFilters(keyword: String?, category: String?, regions: List<String>) {
        val data = restaurants.value ?: return
        filteredRestaurants.value = data.filter { restaurant ->
            val matchesKeyword = keyword.isNullOrEmpty() || restaurant.name.contains(keyword, ignoreCase = true)
            val matchesCategory = category.isNullOrEmpty() || restaurant.category == category
            val matchesRegion = regions.isEmpty() || restaurant.location in regions
            matchesKeyword && matchesCategory && matchesRegion
        }
    }
}
