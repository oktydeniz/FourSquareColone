package com.oktydeniz.k_foursquare.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.oktydeniz.k_foursquare.databinding.ActivityAddNewPlaceBinding
import com.oktydeniz.k_foursquare.util.Utils
import okhttp3.internal.Util

class AddNewPlace : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewPlaceBinding
    private var selectedPictureUri: Uri? = null
    private var selectedBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun selectImage(view: View) {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1002)
        }
    }

    fun goToNext(view: View) {
        Utils.atmosphereText = binding.atmosphereText.text.toString()
        Utils.placeName = binding.placeName.text.toString()
        Utils.placeType = binding.placeTypeText.text.toString()
        Utils.selectedImage = selectedBitmap
        startActivity(Intent(this, MapsActivity::class.java))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1002)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPictureUri = data.data
            try {
                if (selectedPictureUri != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(
                            this.contentResolver,
                            selectedPictureUri!!
                        )
                        selectedBitmap = ImageDecoder.decodeBitmap(source)
                        binding.placeImage.setImageBitmap(selectedBitmap)
                    } else {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            selectedPictureUri
                        )
                        binding.placeImage.setImageBitmap(selectedBitmap)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}