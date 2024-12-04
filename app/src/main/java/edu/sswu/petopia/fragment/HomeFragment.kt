package edu.sswu.petopia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import edu.sswu.petopia.R
import edu.sswu.petopia.SearchActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // 카테고리 아이콘 설정
        val koreanFoodIcon: ImageView = rootView.findViewById(R.id.icon_korean_food)
        val japaneseFoodIcon: ImageView = rootView.findViewById(R.id.icon_japanese_food)
        val westernFoodIcon: ImageView = rootView.findViewById(R.id.icon_western_food)
        val chineseFoodIcon: ImageView = rootView.findViewById(R.id.icon_chinese_food)
        val pubIcon: ImageView = rootView.findViewById(R.id.icon_pub)
        val cafeIcon: ImageView = rootView.findViewById(R.id.icon_cafe)

        // 각 아이콘 클릭 리스너
        koreanFoodIcon.setOnClickListener { navigateToSearchActivity("한식") }
        japaneseFoodIcon.setOnClickListener { navigateToSearchActivity("일식") }
        westernFoodIcon.setOnClickListener { navigateToSearchActivity("양식") }
        chineseFoodIcon.setOnClickListener { navigateToSearchActivity("중식") }
        pubIcon.setOnClickListener { navigateToSearchActivity("펍") }
        cafeIcon.setOnClickListener { navigateToSearchActivity("카페") }

        return rootView
    }

    private fun navigateToSearchActivity(category: String) {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
}
