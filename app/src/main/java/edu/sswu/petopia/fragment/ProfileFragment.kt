package edu.sswu.petopia.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import edu.sswu.petopia.R
import edu.sswu.petopia.ui.login.LoginActivity
import edu.sswu.petopia.ui.profile.EditUserActivity
import edu.sswu.petopia.ui.profile.EditPetActivity

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    // UI 요소 선언
    private lateinit var userImageView: ImageView
    private lateinit var petImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var petNameTextView: TextView
    private lateinit var petBreedTextView: TextView
    private lateinit var petGenderTextView: TextView
    private lateinit var petAgeTextView: TextView

    // 저장된 이미지 URI를 유지하기 위한 변수
    private var savedUserImageUri: String? = null
    private var savedPetImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("UserProfile", MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FirebaseAuth 및 SharedPreferences 초기화
        auth = FirebaseAuth.getInstance()

        // UI 요소 참조

        userImageView = view.findViewById(R.id.userImageView)
        petImageView = view.findViewById(R.id.petImageView)
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        petNameTextView = view.findViewById(R.id.petNameTextView)
        petBreedTextView = view.findViewById(R.id.petBreedTextView)
        petGenderTextView = view.findViewById(R.id.petGenderTextView)
        petAgeTextView = view.findViewById(R.id.petAgeTextView)

        val userEmail = auth.currentUser?.email ?: "example@example.com"
        emailTextView.text = userEmail

        // 버튼 및 클릭 리스너 설정
        view.findViewById<ImageView>(R.id.editUser).setOnClickListener {
            val intent = Intent(requireActivity(), EditUserActivity::class.java)
            requireActivity().startActivity(intent)
        }

        view.findViewById<ImageView>(R.id.editPet).setOnClickListener {
            val intent = Intent(requireActivity(), EditPetActivity::class.java)
            requireActivity().startActivity(intent)
        }

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear() // 모든 데이터 삭제
            editor.apply()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // UI 업데이트
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        // Fragment가 다시 보일 때 데이터 업데이트
        updateUI()
    }

    private fun updateUI() {
        // SharedPreferences에서 사용자 데이터 읽기
        nameTextView.text = "${sharedPreferences.getString("userName", "User")}님 마이페이지"
        emailTextView.text = sharedPreferences.getString("userEmail", "example@example.com")

        // SharedPreferences에서 반려동물 데이터 읽기
        petNameTextView.text = sharedPreferences.getString("petName", "No Pet")
        petBreedTextView.text = sharedPreferences.getString("petBreed", "Unknown Breed")
        petGenderTextView.text = sharedPreferences.getString("petGender", "Unknown Gender")
        petAgeTextView.text = sharedPreferences.getString("petAge", "Unknown Age")

        // 사용자 프로필 이미지 업데이트
        savedUserImageUri = sharedPreferences.getString("userProfileImage", null)
        if (!savedUserImageUri.isNullOrEmpty()) {
            val uri = Uri.parse(savedUserImageUri)
            setImageFromUri(userImageView, uri, R.drawable.ic_pfuser)
        } else {
            userImageView.setImageResource(R.drawable.ic_pfuser) // 기본 이미지 설정
        }

        // 반려동물 프로필 이미지 업데이트
        savedPetImageUri = sharedPreferences.getString("petImageUri", null)
        if (!savedPetImageUri.isNullOrEmpty()) {
            val uri = Uri.parse(savedPetImageUri)
            setImageFromUri(petImageView, uri, R.drawable.ic_pfpet)
        } else {
            petImageView.setImageResource(R.drawable.ic_pfpet) // 기본 반려동물 이미지 설정
        }
    }

    private fun setImageFromUri(imageView: ImageView, uri: Uri, defaultResId: Int) {
        imageView.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                val width = imageView.width
                val height = imageView.height

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                    val drawable = ImageDecoder.decodeDrawable(source)

                    // 이미지를 스케일링하여 ImageView 크기에 맞게 설정
                    val scaledDrawable = scaleDrawableToFit(drawable, width, height)
                    imageView.setImageDrawable(scaledDrawable)
                } else {
                    imageView.setImageURI(uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                imageView.setImageResource(defaultResId) // 기본 이미지 설정
            }
        }
    }

    private fun scaleDrawableToFit(drawable: Drawable, targetWidth: Int, targetHeight: Int): Drawable {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
        return BitmapDrawable(resources, scaledBitmap)
    }
}
