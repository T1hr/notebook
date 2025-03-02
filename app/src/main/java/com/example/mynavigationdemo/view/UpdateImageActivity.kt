package com.example.mynavigationdemo.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mynavigationdemo.databinding.ActivityUpdateImageBinding
import com.example.mynavigationdemo.model.MyImages
import com.example.mynavigationdemo.util.ConvertImage
import com.example.mynavigationdemo.viewmodel.MyImagesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateImageBinding
    private lateinit var viewModel: MyImagesViewModel
    private lateinit var activityResultLauncherForSelectImage: ActivityResultLauncher<Intent>
    private var selectedImage: Bitmap? = null
    private var imageId = -1
    private var imageAsString = ""
    private var imageUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MyImagesViewModel::class.java]

        getAndSetData()
        registerActivityForSelectImage()

        binding.imageViewUpdateImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncherForSelectImage.launch(intent)
        }

        binding.btnUpdate.setOnClickListener {
            binding.btnUpdate.text = "正在保存……"
            binding.btnUpdate.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {
                val updatedTitle = binding.edtUpdateTitle.text.toString()
                val updatedDescription = binding.edtUpdateDescription.text.toString()
                if (imageUpdated && selectedImage != null) {
                    val newImageAsString = ConvertImage.convertToString(selectedImage!!)
                    if (newImageAsString != null) {
                        imageAsString = newImageAsString
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "出错了", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                val updatedImage = MyImages(updatedTitle, updatedDescription, imageAsString).apply {
                    imageId = this@UpdateImageActivity.imageId
                }

                viewModel.update(updatedImage)

                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }

    }

    private fun getAndSetData() {
        imageId = intent.getIntExtra("id", -1)
        if (imageId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                val myImage = viewModel.getItemById(imageId)
                withContext(Dispatchers.Main) {
                    binding.edtUpdateTitle.setText(myImage.imageTitle)
                    binding.edtUpdateDescription.setText(myImage.imageDescription)
                    imageAsString = myImage.imageAsString
                    val imageAsBitmap = ConvertImage.convertToBitmap(imageAsString)
                    binding.imageViewUpdateImage.setImageBitmap(imageAsBitmap)
                }
            }
        }
    }

    private fun registerActivityForSelectImage() {
        activityResultLauncherForSelectImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val imageUri = result.data?.data
                    imageUri?.let {
                        selectedImage = if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            MediaStore.Images.Media.getBitmap(contentResolver, it)
                        }
                        binding.imageViewUpdateImage.setImageBitmap(selectedImage)
                        imageUpdated = true
                    }
                }
            }
    }
}
