package edu.sswu.petopia.ui.profile

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import edu.sswu.petopia.R

class EditUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // backBtn 클릭 리스너 설정
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // 이전 화면으로 돌아가기
            onBackPressed()  // onBackPressed()를 호출하면 자동으로 ProfileFragment로 돌아갑니다.
        }
    }
}
