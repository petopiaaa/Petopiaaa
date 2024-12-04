package edu.sswu.petopia.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.sswu.petopia.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class EditPetActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profileImageView: ImageView
    private var selectedImageUri: Uri? = null
    @SuppressLint("MissingInflatedId")
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
        val petWeightEditText: EditText = findViewById(R.id.petWeightEditText)
        val saveButton: Button = findViewById(R.id.saveBtn)
        profileImageView = findViewById(R.id.petpfImage)

        // SharedPreferences에서 이전 값 불러오기
        petNameEditText.setText(sharedPreferences.getString("petName", ""))
        petBreedEditText.setText(sharedPreferences.getString("petBreed", ""))
        petGenderEditText.setText(sharedPreferences.getString("petGender", ""))
        petWeightEditText.setText(sharedPreferences.getString("petWeight", ""))
        petAgeEditText.setText("") // 나이는 다시 계산해야 하므로 초기화

        val savedImageUri = sharedPreferences.getString("petImageUri", null)
        if (savedImageUri != null) {
            selectedImageUri = Uri.parse(savedImageUri)
            setImageFromUri(selectedImageUri!!)
        } else {
            profileImageView.setImageResource(R.drawable.ic_pfadd2)
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
            val petName = petNameEditText.text.toString()
            val petBreed = petBreedEditText.text.toString()
            val petGender = petGenderEditText.text.toString()
            val petWeight = petWeightEditText.text.toString()
            val petAgeInput = petAgeEditText.text.toString()

            if (petAgeInput.isNotEmpty()) {
                try {
                    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                    val birthDate = dateFormat.parse(petAgeInput)

                    if (birthDate != null) {
                        val birthCalendar = Calendar.getInstance().apply { time = birthDate }
                        val currentCalendar = Calendar.getInstance()
                        val ageInYears =
                            currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                        val ageInMonths =
                            currentCalendar.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)
                        val adjustedYears = if (ageInMonths < 0) ageInYears - 1 else ageInYears
                        val adjustedMonths = if (ageInMonths < 0) 12 + ageInMonths else ageInMonths
                        val calculatedAge = "${adjustedYears}년 ${adjustedMonths}개월"

                        // SharedPreferences 저장
                        val editor = sharedPreferences.edit()
                        editor.putString("petName", petName)
                        editor.putString("petBreed", petBreed)
                        editor.putString("petGender", petGender)
                        editor.putString("petWeight", petWeight)
                        editor.putString("petAge", calculatedAge)
                        if (selectedImageUri != null) {
                            editor.putString("petImageUri", selectedImageUri.toString())
                        }
                        editor.apply()

                        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                        finish() // 현재 액티비티 종료
                    } else {
                        Toast.makeText(this, "유효한 생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "생년월일 형식을 확인해주세요 (예: 2012.12.30)", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        // backBtn 클릭 리스너 설정
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // 이전 화면으로 돌아가기
            finish()
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        ActivityCompat.requestPermissions(this, arrayOf(permission), 1002)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1002 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
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
