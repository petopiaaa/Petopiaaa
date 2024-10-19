package edu.sswu.petopia

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.sswu.petopia.fragment.HomeFragment
import edu.sswu.petopia.fragment.MapFragment
import edu.sswu.petopia.fragment.NearbyFragment
import edu.sswu.petopia.fragment.ProfileFragment
import edu.sswu.petopia.fragment.SearchFragment


class MainActivity<EditText : View?> : AppCompatActivity() {

    // FirebaseAuth 의 인스턴스를 선언
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigationBar()

        // onCreate() 메서드에서 FirebaseAuth 인스턴스를 초기화시키기
        auth = Firebase.auth


        // 회원가입 기능
        val joinBtn = findViewById<Button>(R.id.joinBtn)
        joinBtn.setOnClickListener {

            val email = findViewById<EditText>(R.id.email)
            val password = findViewById<EditText>(R.id.password)

            auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()
                    }
                }

        }

    }



    private fun setupNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_search -> SearchFragment()
                R.id.menu_nearby -> NearbyFragment()
                R.id.menu_map -> MapFragment()
                R.id.menu_profile -> ProfileFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()

            true
        }
    }
}