package com.resocoder.firemessage.fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.resocoder.firemessage.R
import com.resocoder.firemessage.SignInActivity
import com.resocoder.firemessage.glide.GlideApp
import com.resocoder.firemessage.util.FirestoreUtil
import com.resocoder.firemessage.util.StorageUtil
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import java.io.ByteArrayOutputStream


class MyAccountFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        view.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }

            btn_save.setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(editText_name.text.toString(),
                                editText_bio.text.toString(), imagePath)
                    }
                else
                    FirestoreUtil.updateCurrentUser(editText_name.text.toString(),
                            editText_bio.text.toString(), null)
                Toast.makeText(requireContext(), "Saving", Toast.LENGTH_SHORT).show()
            }

            btn_sign_out.setOnClickListener {
                AuthUI.getInstance()
                        .signOut(requireContext())
                        .addOnCompleteListener {
                            startActivity(Intent(requireContext(), SignInActivity::class.java))
                        }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                // To handle deprication use
                val source =
                        ImageDecoder.createSource(activity?.contentResolver!!, selectedImagePath!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                // Use older version
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)
            }
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(requireContext())
                    .load(selectedImageBytes)
                    .into(imageView_profile_picture)

            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            if (this@MyAccountFragment.isVisible) {
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if (!pictureJustChanged && user.profilePicturePath != null)
                    GlideApp.with(requireContext())
                            .load(StorageUtil.pathToReference(user.profilePicturePath))
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .into(imageView_profile_picture)
            }
        }
    }

}
