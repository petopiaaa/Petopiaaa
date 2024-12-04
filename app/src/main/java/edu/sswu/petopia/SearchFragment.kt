package edu.sswu.petopia

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.CheckBox
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchFragment : Fragment() {

    private val selectedRegions = mutableListOf<String>() // 선택된 지역 목록
    private lateinit var selectedFiltersTextView: TextView // 선택된 필터 표시 텍스트뷰

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

        val searchBar: EditText = rootView.findViewById(R.id.search_bar)
        val searchIcon: ImageView = rootView.findViewById(R.id.search_icon)
        val regionIcon: ImageView = rootView.findViewById(R.id.icon_region)
        val spaceIcon: ImageView = rootView.findViewById(R.id.icon_space)
        val weightIcon: ImageView = rootView.findViewById(R.id.icon_weight)
        selectedFiltersTextView = rootView.findViewById(R.id.selected_filters)

        // 검색 버튼 클릭 이벤트
        searchIcon.setOnClickListener {
            val query = searchBar.text.toString()
            if (query.isNotEmpty()) {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                intent.putExtra("keyword", query) // 검색어 전달
                intent.putStringArrayListExtra("selectedRegions", ArrayList(selectedRegions)) // 지역 전달
                startActivity(intent)
            }
        }

        // 지역 아이콘 클릭 이벤트
        regionIcon.setOnClickListener {
            showRegionBottomSheet()
        }

        // 공간 및 몸무게 아이콘 클릭 이벤트
        spaceIcon.setOnClickListener {
            showSpaceBottomSheet()
        }
        weightIcon.setOnClickListener {
            showWeightBottomSheet()
        }

        return rootView
    }

    private fun showRegionBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_region, null)
        dialog.setContentView(view)

        // 체크박스 참조
        val checkboxDobong = view.findViewById<CheckBox>(R.id.checkbox_dobong)
        val checkboxSeongbuk = view.findViewById<CheckBox>(R.id.checkbox_seongbuk)
        val checkboxNowon = view.findViewById<CheckBox>(R.id.checkbox_nowon)
        val checkboxGangbuk = view.findViewById<CheckBox>(R.id.checkbox_gangbuk)

        // 기존 선택 상태 복원
        checkboxDobong.isChecked = selectedRegions.contains("도봉구")
        checkboxSeongbuk.isChecked = selectedRegions.contains("성북구")
        checkboxNowon.isChecked = selectedRegions.contains("노원구")
        checkboxGangbuk.isChecked = selectedRegions.contains("강북구")

        // 적용 버튼
        val applyButton = view.findViewById<View>(R.id.apply_region_button)
        applyButton.setOnClickListener {
            updateSelectedRegions(
                checkboxDobong,
                checkboxSeongbuk,
                checkboxNowon,
                checkboxGangbuk
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateSelectedRegions(
        checkboxDobong: CheckBox,
        checkboxSeongbuk: CheckBox,
        checkboxNowon: CheckBox,
        checkboxGangbuk: CheckBox
    ) {
        // 선택된 지역 업데이트
        selectedRegions.clear()
        if (checkboxDobong.isChecked) selectedRegions.add("도봉구")
        if (checkboxSeongbuk.isChecked) selectedRegions.add("성북구")
        if (checkboxNowon.isChecked) selectedRegions.add("노원구")
        if (checkboxGangbuk.isChecked) selectedRegions.add("강북구")

        updateSelectedFilters()
    }

    private fun updateSelectedFilters() {
        // 선택된 필터 표시 업데이트
        if (selectedRegions.isEmpty()) {
            selectedFiltersTextView.visibility = View.GONE
        } else {
            selectedFiltersTextView.visibility = View.VISIBLE
            selectedFiltersTextView.text = "선택된 지역: ${selectedRegions.joinToString(", ")}"
        }
    }

    private fun showSpaceBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_space, null)
        dialog.setContentView(view)

        val applyButton = view.findViewById<View>(R.id.apply_space_button)
        applyButton.setOnClickListener {
            dialog.dismiss()
            // 공간 데이터 처리 추가
        }

        dialog.show()
    }

    private fun showWeightBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_weight, null)
        dialog.setContentView(view)

        val applyButton = view.findViewById<View>(R.id.apply_weight_button)
        applyButton.setOnClickListener {
            dialog.dismiss()
            // 몸무게 데이터 처리 추가
        }

        dialog.show()
    }
}
