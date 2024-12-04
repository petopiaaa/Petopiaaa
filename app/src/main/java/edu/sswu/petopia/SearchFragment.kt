package edu.sswu.petopia

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import edu.sswu.petopia.R
import edu.sswu.petopia.SearchActivity


class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        val searchBar: EditText = rootView.findViewById(R.id.search_bar)
        val searchIcon: ImageView = rootView.findViewById(R.id.search_icon)

        searchIcon.setOnClickListener {
            val query = searchBar.text.toString()
            if (query.isNotEmpty()) {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                intent.putExtra("keyword", query) // 검색어 전달
                startActivity(intent)
            }
        }

        return rootView
    }

}
