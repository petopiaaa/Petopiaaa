package edu.sswu.petopia.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.sswu.petopia.R
import edu.sswu.petopia.ui.login.LoginActivity
import edu.sswu.petopia.ui.profile.EditUserActivity
import edu.sswu.petopia.ui.profile.EditPetActivity

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    // UI 요소 선언
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var petNameTextView: TextView
    private lateinit var petBreedTextView: TextView
    private lateinit var petGenderTextView: TextView
    private lateinit var petAgeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("UserProfile", MODE_PRIVATE)

        // UI 요소 참조
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        petNameTextView = view.findViewById(R.id.petNameTextView)
        petBreedTextView = view.findViewById(R.id.petBreedTextView)
        petGenderTextView = view.findViewById(R.id.petGenderTextView)
        petAgeTextView = view.findViewById(R.id.petAgeTextView)

        // 초기 데이터 표시
        updateUI()

        // editUser 클릭 리스너 설정
        val editUserButton = view.findViewById<ImageView>(R.id.editUser)
        editUserButton.setOnClickListener {
            // EditUserActivity로 이동
            val intent = Intent(requireContext(), EditUserActivity::class.java)
            startActivity(intent)
        }

        // editPet 클릭 리스너 설정
        val editPetButton = view.findViewById<ImageView>(R.id.editPet)
        editPetButton.setOnClickListener {
            // EditPetActivity로 이동
            val intent = Intent(requireContext(), EditPetActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 동작
        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear() // 모든 데이터 삭제
            editor.apply()

            // LoginActivity로 이동
            val intent = Intent(requireContext(), LoginActivity::class.java) // requireContext() 사용
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Fragment로 돌아왔을 때 UI 업데이트
        updateUI()
    }

    private fun updateUI() {
        // SharedPreferences에서 데이터 읽기
        nameTextView.text = "${sharedPreferences.getString("userName", "User")}님 마이페이지"
        emailTextView.text = sharedPreferences.getString("userEmail", "example@example.com")
        petNameTextView.text = sharedPreferences.getString("petName", "No Pet")
        petBreedTextView.text = sharedPreferences.getString("petBreed", "Unknown Breed")
        petGenderTextView.text = sharedPreferences.getString("petGender", "Unknown Gender")
        petAgeTextView.text = sharedPreferences.getString("petAge", "Unknown Age")
    }

}