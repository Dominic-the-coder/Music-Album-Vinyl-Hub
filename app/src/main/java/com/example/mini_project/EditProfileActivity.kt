package com.example.mini_project

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.ImageDTO
import com.example.mini_project.backend.UserDTO
import com.example.mini_project.instance.RetrofitInstance
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var btnChangePhoto: MaterialButton

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var btnSave: MaterialButton

    private var selectedImageUri: Uri? = null

    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        imgProfile = findViewById(R.id.imgProfile)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)

        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        btnSave = findViewById(R.id.btnSave)

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        loadUser()

        btnChangePhoto.setOnClickListener {
            openGallery()
        }

        btnSave.setOnClickListener {
            updateProfile()
        }
    }

    // =========================
    // LOAD USER
    // =========================

    private fun loadUser() {

        val userId = Auth.getUserId(this)

        if (userId == -1) {

            Toast.makeText(
                this,
                "User not logged in",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        RetrofitInstance.getApi(this)
            .getUserById(userId)
            .enqueue(object : Callback<UserDTO> {

                override fun onResponse(
                    call: Call<UserDTO>,
                    response: Response<UserDTO>
                ) {

                    if (response.isSuccessful) {

                        val user = response.body()

                        if (user != null) {

                            edtName.setText(user.name)
                            edtEmail.setText(user.email)

                            // Load saved profile image
                            if (user.imageId != null) {
                                loadProfileImage(user.imageId)
                            }
                        }

                    } else {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Failed to load profile: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<UserDTO>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@EditProfileActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =========================
    // LOAD PROFILE IMAGE
    // =========================

    private fun loadProfileImage(imageId: Int) {

        RetrofitInstance
            .getApi(this)
            .getImage(imageId)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {

                        val body = response.body()

                        if (body != null) {

                            try {

                                val bytes = body.bytes()

                                val bitmap = BitmapFactory.decodeByteArray(
                                    bytes,
                                    0,
                                    bytes.size
                                )

                                if (bitmap != null) {

                                    imgProfile.setImageBitmap(bitmap)

                                } else {

                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        "Could not decode image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Image error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    } else {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Image failed: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@EditProfileActivity,
                        "Image error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =========================
    // UPDATE PROFILE
    // =========================

    private fun updateProfile() {

        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        if (name.isEmpty()) {

            edtName.error = "Enter your name"
            return
        }

        if (email.isEmpty()) {

            edtEmail.error = "Enter your email"
            return
        }

        val userId = Auth.getUserId(this)

        if (userId == -1) {

            Toast.makeText(
                this,
                "User not logged in",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val request = UserDTO(
            id = userId,
            name = name,
            email = email
        )

        btnSave.isEnabled = false

        RetrofitInstance.getApi(this)
            .updateUser(
                userId,
                request
            )
            .enqueue(object : Callback<UserDTO> {

                override fun onResponse(
                    call: Call<UserDTO>,
                    response: Response<UserDTO>
                ) {

                    btnSave.isEnabled = true

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profile updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Failed to update profile: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<UserDTO>,
                    t: Throwable
                ) {

                    btnSave.isEnabled = true

                    Toast.makeText(
                        this@EditProfileActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =========================
    // OPEN GALLERY
    // =========================

    private fun openGallery() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(
            intent,
            PICK_IMAGE
        )
    }

    // =========================
    // IMAGE SELECTED
    // =========================

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == PICK_IMAGE &&
            resultCode == Activity.RESULT_OK
        ) {

            selectedImageUri = data?.data

            selectedImageUri?.let { uri ->

                // Show selected image immediately
                imgProfile.setImageURI(uri)

                // Upload image
                uploadImage(uri)
            }
        }
    }

    // =========================
    // UPLOAD IMAGE
    // =========================

    private fun uploadImage(uri: Uri) {

        val userId = Auth.getUserId(this)

        if (userId == -1) {

            Toast.makeText(
                this,
                "User not logged in",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        try {

            val file = createTempFileFromUri(uri)

            val mimeType =
                contentResolver.getType(uri)
                    ?: "image/jpeg"

            val requestFile = file.asRequestBody(
                mimeType.toMediaTypeOrNull()
            )

            val body = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

            btnChangePhoto.isEnabled = false

            RetrofitInstance.getApi(this)
                .uploadImage(body)
                .enqueue(object : Callback<ImageDTO> {

                    override fun onResponse(
                        call: Call<ImageDTO>,
                        response: Response<ImageDTO>
                    ) {

                        if (response.isSuccessful) {

                            val image = response.body()

                            if (image != null) {

                                // Connect image to user
                                connectImageToUser(
                                    userId,
                                    image.id
                                )

                            } else {

                                btnChangePhoto.isEnabled = true

                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Image upload failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {

                            btnChangePhoto.isEnabled = true

                            Toast.makeText(
                                this@EditProfileActivity,
                                "Upload failed: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ImageDTO>,
                        t: Throwable
                    ) {

                        btnChangePhoto.isEnabled = true

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Upload error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        } catch (e: Exception) {

            btnChangePhoto.isEnabled = true

            Toast.makeText(
                this,
                "Could not prepare image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================
    // CONNECT IMAGE TO USER
    // =========================

    private fun connectImageToUser(
        userId: Int,
        imageId: Int
    ) {

        RetrofitInstance.getApi(this)
            .updateProfileImage(
                userId,
                imageId
            )
            .enqueue(object : Callback<UserDTO> {

                override fun onResponse(
                    call: Call<UserDTO>,
                    response: Response<UserDTO>
                ) {

                    btnChangePhoto.isEnabled = true

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Photo updated",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Image uploaded but could not link to profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<UserDTO>,
                    t: Throwable
                ) {

                    btnChangePhoto.isEnabled = true

                    Toast.makeText(
                        this@EditProfileActivity,
                        "Could not link image: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =========================
    // URI TO FILE
    // =========================

    private fun createTempFileFromUri(uri: Uri): File {

        val file = File.createTempFile(
            "profile_",
            ".jpg",
            cacheDir
        )

        contentResolver
            .openInputStream(uri)
            .use { input ->

                file.outputStream().use { output ->

                    input?.copyTo(output)
                }
            }

        return file
    }
}