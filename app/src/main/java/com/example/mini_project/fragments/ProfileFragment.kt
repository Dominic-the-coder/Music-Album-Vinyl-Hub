package com.example.mini_project.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mini_project.CartActivity
import com.example.mini_project.ChangePasswordActivity
import com.example.mini_project.EditProfileActivity
import com.example.mini_project.LoginActivity
import com.example.mini_project.MainActivity
import com.example.mini_project.R
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.GoogleSignInManager
import com.example.mini_project.backend.ThemeManager
import com.example.mini_project.backend.UserDTO
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.materialswitch.MaterialSwitch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment :
    Fragment(R.layout.fragment_profile) {

    private var txtProfileName: TextView? = null
    private var txtProfileEmail: TextView? = null
    private var imgProfile: ImageView? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupProfile(view)
    }

    override fun onResume() {

        super.onResume()

        if (isAdded) {

            txtProfileName?.let { name ->

                txtProfileEmail?.let { email ->

                    loadProfile(
                        name,
                        email
                    )
                }
            }
        }
    }

    private fun setupProfile(view: View) {

        txtProfileName =
            view.findViewById(R.id.txtProfileName)

        txtProfileEmail =
            view.findViewById(R.id.txtProfileEmail)

        imgProfile =
            view.findViewById(R.id.imgProfile)

        val btnBack =
            view.findViewById<ImageView>(R.id.btnBack)

        val btnEditProfile =
            view.findViewById<ImageView>(R.id.btnEditProfile)

        val btnOrders =
            view.findViewById<View>(R.id.btnOrders)

        val btnPayment =
            view.findViewById<View>(R.id.btnPayment)

        val btnChangePassword =
            view.findViewById<View>(R.id.btnChangePassword)

        val btnAbout =
            view.findViewById<View>(R.id.btnAbout)

        val switchNotification =
            view.findViewById<MaterialSwitch>(
                R.id.switchNotification
            )

        val btnLogout =
            view.findViewById<View>(R.id.btnLogout)

        // =========================
        // BACK
        // =========================

        btnBack.setOnClickListener {

            val bottomNav =
                requireActivity()
                    .findViewById<BottomNavigationView>(
                        R.id.bottomNav
                    )

            bottomNav.selectedItemId =
                R.id.nav_home
        }

        // =========================
        // LOAD PROFILE
        // =========================

        txtProfileName?.let { name ->

            txtProfileEmail?.let { email ->

                loadProfile(
                    name,
                    email
                )
            }
        }

        // =========================
        // EDIT PROFILE
        // =========================

        btnEditProfile.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    EditProfileActivity::class.java
                )
            )
        }

        // =========================
        // ORDERS
        // =========================

        btnOrders.setOnClickListener {

            val mainActivity =
                requireActivity() as MainActivity

            mainActivity.ordersOpenedFromProfile =
                true

            val bottomNav =
                requireActivity()
                    .findViewById<BottomNavigationView>(
                        R.id.bottomNav
                    )

            bottomNav.selectedItemId =
                R.id.nav_order
        }

        // =========================
        // CART
        // =========================

        btnPayment.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    CartActivity::class.java
                )
            )
        }

        // =========================
        // CHANGE PASSWORD
        // =========================

        btnChangePassword.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    ChangePasswordActivity::class.java
                )
            )
        }

        // =========================
        // DARK MODE
        // =========================

        val darkMode =
            ThemeManager.isDarkMode(
                requireContext()
            )

        switchNotification.isChecked =
            darkMode

        switchNotification.setOnCheckedChangeListener {
                _,
                isChecked ->

            ThemeManager.setDarkMode(
                requireContext(),
                isChecked
            )
        }

        // =========================
        // ABOUT
        // =========================

        btnAbout.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Vinyl Hub\nMusic & Vinyl Collection App",
                Toast.LENGTH_LONG
            ).show()
        }

        // =========================
        // LOGOUT
        // =========================

        btnLogout.setOnClickListener {

            val context = requireContext()

            // Clear app login session
            Auth.logout(context)

            // Sign out from Google
            GoogleSignInManager
                .getClient(context)
                .signOut()
                .addOnCompleteListener {

                    val intent =
                        Intent(
                            context,
                            LoginActivity::class.java
                        )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                }
        }
    }

    // =========================
    // LOAD USER PROFILE
    // =========================

    private fun loadProfile(
        txtProfileName: TextView,
        txtProfileEmail: TextView
    ) {

        if (!isAdded) return

        val userId =
            Auth.getUserId(
                requireContext()
            )

        if (userId == -1) {

            txtProfileName.text =
                "Not logged in"

            txtProfileEmail.text =
                ""

            return
        }

        RetrofitInstance
            .getApi(requireContext())
            .getUserById(userId)
            .enqueue(object : Callback<UserDTO> {

                override fun onResponse(
                    call: Call<UserDTO>,
                    response: Response<UserDTO>
                ) {

                    if (!isAdded) return

                    if (response.isSuccessful) {

                        val user =
                            response.body()

                        if (user != null) {

                            txtProfileName.text =
                                user.name

                            txtProfileEmail.text =
                                user.email

                            if (user.imageId != null) {

                                loadProfileImage(
                                    user.imageId
                                )

                            } else {

                                imgProfile?.setImageResource(
                                    R.drawable.ic_person
                                )
                            }
                        }

                    } else {

                        Toast.makeText(
                            requireContext(),
                            "Failed to load profile: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<UserDTO>,
                    t: Throwable
                ) {

                    if (!isAdded) return

                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =========================
    // LOAD PROFILE IMAGE
    // =========================

    private fun loadProfileImage(
        imageId: Int
    ) {

        if (!isAdded) return

        RetrofitInstance
            .getApi(requireContext())
            .getImage(imageId)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (!isAdded) return

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            requireContext(),
                            "Image error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val body =
                        response.body()

                    if (body == null) {

                        Toast.makeText(
                            requireContext(),
                            "Image body is empty",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    try {

                        val bytes =
                            body.bytes()

                        println(
                            "ANDROID IMAGE SIZE: ${bytes.size}"
                        )

                        println(
                            "ANDROID CONTENT TYPE: ${body.contentType()}"
                        )

                        val bitmap =
                            BitmapFactory.decodeByteArray(
                                bytes,
                                0,
                                bytes.size
                            )

                        if (bitmap != null) {

                            imgProfile?.setImageBitmap(
                                bitmap
                            )

                        } else {

                            Toast.makeText(
                                requireContext(),
                                "Android could not decode image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {

                        Toast.makeText(
                            requireContext(),
                            "Image decode error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {

                    if (!isAdded) return

                    Toast.makeText(
                        requireContext(),
                        "Image request failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}