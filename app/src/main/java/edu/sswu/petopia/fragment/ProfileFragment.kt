package edu.sswu.petopia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import edu.sswu.petopia.R
import edu.sswu.petopia.ui.profile.EditUserActivity
import edu.sswu.petopia.ui.profile.EditPetActivity

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

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

        return view
    }
}