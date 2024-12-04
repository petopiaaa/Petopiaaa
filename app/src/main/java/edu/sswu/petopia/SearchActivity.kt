package edu.sswu.petopia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var adapter: RestaurantAdapter
    private lateinit var recyclerView: RecyclerView

    private val selectedRegions = mutableListOf<String>() // 선택된 지역 필터
    private lateinit var selectedFiltersTextView: TextView // 선택된 필터 표시 텍스트뷰
    private var selectedCategory: String? = null // 선택된 카테고리
    private var selectedKeyword: String? = null // 선택된 키워드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 뒤로가기 버튼 설정
        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        selectedFiltersTextView = findViewById(R.id.selected_filters)

        // 지역 필터 버튼
        val regionButton: Button = findViewById(R.id.btn_region)
        regionButton.setOnClickListener {
            showRegionBottomSheet() // 지역 선택 Bottom Sheet 열기
        }

        // 카테고리 버튼 클릭 이벤트 설정
        setupCategoryButtons()

        initRecyclerView()
        initViewModel()
        setupSearchBar()
        handleIntents() // Intents를 처리하여 필터링 수행
    }

    private fun initRecyclerView() {
        adapter = RestaurantAdapter(emptyList()) { restaurant ->
            navigateToDetail(restaurant)
        }
        recyclerView = findViewById(R.id.result_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[SearchActivityViewModel::class.java]

        // 데이터를 가져온 후 UI 업데이트 및 필터링
        viewModel.fetchRestaurants {
            updateUI() // 데이터 로드 후 UI 업데이트
            handleIntents() // Intents 처리
        }
    }

    private fun updateUI() {
        viewModel.filteredRestaurants.observe(this) { data ->
            adapter.updateData(data)
        }
    }

    private fun setupSearchBar() {
        val searchBar: EditText = findViewById(R.id.search_bar)
        val searchIcon: ImageView = findViewById(R.id.search_icon)

        searchIcon.setOnClickListener {
            val keyword = searchBar.text.toString()
            selectedKeyword = keyword
            viewModel.applyFilters(selectedRegions, selectedCategory, selectedKeyword)
        }
    }

    private fun handleIntents() {
        val category = intent.getStringExtra("category")
        val keyword = intent.getStringExtra("keyword")
        val intentRegions = intent.getStringArrayListExtra("selectedRegions")

        val searchBar: EditText = findViewById(R.id.search_bar)

        // 선택된 지역 유지
        if (!intentRegions.isNullOrEmpty()) {
            selectedRegions.clear()
            selectedRegions.addAll(intentRegions)
            updateSelectedFilters()
        }

        when {
            !category.isNullOrEmpty() -> {
                selectedCategory = category // 선택된 카테고리 업데이트
                viewModel.applyFilters(selectedRegions, selectedCategory, searchBar.text.toString())
            }
            !keyword.isNullOrEmpty() -> {
                searchBar.setText(keyword) // 검색어를 EditText에 설정
                viewModel.applyFilters(selectedRegions, selectedCategory, keyword)
            }
        }
    }

    private fun setupCategoryButtons() {
        val categoryButtons = listOf(
            Pair(findViewById<Button>(R.id.btn_all), null), // 전체
            Pair(findViewById<Button>(R.id.btn_korean), "한식"),
            Pair(findViewById<Button>(R.id.btn_japanese), "일식"),
            Pair(findViewById<Button>(R.id.btn_western), "양식")
        )

        categoryButtons.forEach { (button, category) ->
            button.setOnClickListener {
                selectedCategory = category // 선택된 카테고리 업데이트
                viewModel.applyFilters(selectedRegions, selectedCategory, selectedKeyword) // 필터링 적용
            }
        }
    }

    private fun navigateToDetail(restaurant: Restaurant) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("name", restaurant.name)
        intent.putExtra("menu", restaurant.menu)
        intent.putExtra("address", restaurant.address)
        intent.putExtra("address22", restaurant.address22)
        intent.putExtra("hours", restaurant.hours)
        intent.putExtra("contact", restaurant.contact)
        intent.putExtra("description", restaurant.description)
        intent.putExtra("image_url", restaurant.imageUrl)
        startActivity(intent)
    }

    private fun showRegionBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_region, null)
        dialog.setContentView(view)

        // 지역 선택 체크박스
        val dobong = view.findViewById<CheckBox>(R.id.checkbox_dobong)
        val seongbuk = view.findViewById<CheckBox>(R.id.checkbox_seongbuk)
        val nowon = view.findViewById<CheckBox>(R.id.checkbox_nowon)
        val gangbuk = view.findViewById<CheckBox>(R.id.checkbox_gangbuk)

        // 기존 선택 상태 복원
        dobong.isChecked = selectedRegions.contains("도봉구")
        seongbuk.isChecked = selectedRegions.contains("성북구")
        nowon.isChecked = selectedRegions.contains("노원구")
        gangbuk.isChecked = selectedRegions.contains("강북구")

        // 적용 버튼
        val applyButton = view.findViewById<Button>(R.id.apply_region_button)
        applyButton.setOnClickListener {
            selectedRegions.clear()
            if (dobong.isChecked) selectedRegions.add("도봉구")
            if (seongbuk.isChecked) selectedRegions.add("성북구")
            if (nowon.isChecked) selectedRegions.add("노원구")
            if (gangbuk.isChecked) selectedRegions.add("강북구")

            updateSelectedFilters()
            viewModel.applyFilters(selectedRegions, selectedCategory, selectedKeyword) // 필터 재적용

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateSelectedFilters() {
        if (selectedRegions.isEmpty()) {
            selectedFiltersTextView.visibility = View.GONE
        } else {
            selectedFiltersTextView.visibility = View.VISIBLE
            selectedFiltersTextView.text = "선택된 지역: ${selectedRegions.joinToString(", ")}"
        }
    }
}
