package edu.sswu.petopia.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import edu.sswu.petopia.R

class EditPetActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)  // 레이아웃 파일 연결

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        // EditText와 버튼 참조
        val petNameEditText: EditText = findViewById(R.id.petNameEditText)
        val petBreedEditText: EditText = findViewById(R.id.petBreedEditText)
        val petGenderEditText: EditText = findViewById(R.id.petGenderEditText)
        val petAgeEditText: EditText = findViewById(R.id.petAgeEditText)
        val saveButton: Button = findViewById(R.id.saveBtn)

        // 저장 버튼 클릭 리스너
        saveButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("petName", petNameEditText.text.toString())
            editor.putString("petBreed", petBreedEditText.text.toString())
            editor.putString("petGender", petGenderEditText.text.toString())
            editor.putString("petAge", petAgeEditText.text.toString())
            editor.apply()

            // ProfileActivity로 이동
            onBackPressed()
        }

        // backBtn 클릭 리스너 설정
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // 이전 화면으로 돌아가기
            onBackPressed()  // onBackPressed()를 호출하면 자동으로 ProfileFragment로 돌아갑니다.
        }
    }
}
