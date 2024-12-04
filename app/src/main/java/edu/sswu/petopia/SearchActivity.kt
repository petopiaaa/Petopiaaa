package edu.sswu.petopia

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var adapter: RestaurantAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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

        // 데이터를 가져온 후 UI 업데이트
        viewModel.fetchRestaurants {
            updateUI() // 데이터 로드 후 필터링 반영
            handleCategoryIntent() // 카테고리 필터링 적용
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
            if (keyword.isNotEmpty()) {
                viewModel.filterByKeyword(keyword)
            } else {
                Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleIntents() {
        val category = intent.getStringExtra("category")
        val keyword = intent.getStringExtra("keyword")

        when {
            !category.isNullOrEmpty() -> viewModel.filterByCategory(category) // 필터링 카테고리 설정
            !keyword.isNullOrEmpty() -> viewModel.filterByKeyword(keyword) // 키워드 필터링
        }
    }

    private fun handleCategoryIntent() {
        val category = intent.getStringExtra("category")
        if (!category.isNullOrEmpty()) {
            viewModel.filterByCategory(category) // 카테고리 필터링
        }
    }

    private fun navigateToDetail(restaurant: Restaurant) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("restaurant_id", restaurant.id)
        startActivity(intent)
    }
}
