package edu.sswu.petopia.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import edu.sswu.petopia.R
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

class EditUserActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var profileImageView: ImageView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // FirebaseAuth 및 SharedPreferences 초기화
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        // UI 요소 참조
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val saveButton: Button = findViewById(R.id.saveBtn)
        profileImageView = findViewById(R.id.userpfImage)

        // FirebaseAuth에서 현재 사용자 이메일 가져오기
        val userEmail = auth.currentUser?.email ?: "example@example.com"
        // SharedPreferences에서 이름 가져오기
        val savedName = sharedPreferences.getString("userName", "")

        val savedImageUri = sharedPreferences.getString("userProfileImage", null)

        // EditText에 저장된 값 설정
        nameEditText.setText(savedName)
        emailEditText.setText(userEmail)
        emailEditText.isEnabled = false // 이메일 수정 불가

        // 이미지가 있으면 설정, 없으면 기본 이미지 설정
        if (savedImageUri != null) {
            selectedImageUri = Uri.parse(savedImageUri)
            setImageFromUri(selectedImageUri!!)
        } else {
            profileImageView.setImageResource(R.drawable.ic_pfadd2) // 기본 이미지 설정
        }

        // 이미지 클릭 리스너
        profileImageView.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestPermission()
            }
        }

        // 저장 버튼 클릭 리스너
        saveButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("userName", nameEditText.text.toString())
            if (selectedImageUri != null) {
                editor.putString("userProfileImage", selectedImageUri.toString())
            } else {
                // 기본 이미지 저장
                editor.putString("userProfileImage", profileImageView.toString())
            }
            editor.apply()

            finish() // 현재 액티비티 종료
        }

        // backBtn 클릭 리스너 설정
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // 이전 화면으로 돌아가기
            finish()
        }
    }

    // 권한 확인
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 권한 요청
    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        ActivityCompat.requestPermissions(this, arrayOf(permission), 1001)
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()  // 권한이 승인되었으면 갤러리 열기
        } else {
            Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 갤러리 열기
    private fun openGallery() {
        // SAF 방식으로 갤러리 열기
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        galleryLauncher.launch(intent)
    }

    // 이미지 선택 후 SAF 방식으로 URI 처리
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    setImageFromUri(it)
                }
            }
        }

    // URI에서 이미지를 안전하게 설정하는 함수
    private fun setImageFromUri(uri: Uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                val drawable = ImageDecoder.decodeDrawable(source)
                // 이미지의 크기를 맞추기 위해 비율을 유지하면서 ImageView에 설정
                val width = profileImageView.width
                val height = profileImageView.height
                val scaledDrawable = scaleDrawableToFit(drawable, width, height)
                profileImageView.setImageDrawable(scaledDrawable)
            } else {
                profileImageView.setImageURI(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "이미지 로드 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scaleDrawableToFit(drawable: Drawable, targetWidth: Int, targetHeight: Int): Drawable {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
        return BitmapDrawable(resources, scaledBitmap)
    }

}
