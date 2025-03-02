package com.example.mynavigationdemo.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mynavigationdemo.databinding.ActivityAddImageBinding
import com.example.mynavigationdemo.model.MyImages
import com.example.mynavigationdemo.util.ControlPermission
import com.example.mynavigationdemo.util.ConvertImage
import com.example.mynavigationdemo.viewmodel.MyImagesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddImageActivity : AppCompatActivity() {
    lateinit var addImageBinding: ActivityAddImageBinding
    lateinit var activityResultLauncherForSelectImage: ActivityResultLauncher<Intent>
    lateinit var selectedImage: Bitmap
    lateinit var myImagesViewModel: MyImagesViewModel
    //用于判断当前是否正在存取数据库
    var saveFlag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        //初始化视图绑定
        addImageBinding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(addImageBinding.root)
        //获取ViewModel引用
        myImagesViewModel = ViewModelProvider(this)[MyImagesViewModel::class.java]
        //注册回调方法，获取用户从MediaStore中选择的图片
        registerActivityForSelectImage()
        //用户点击Activity上的图片，说明他想选择要收藏的图片
        addImageBinding.imageViewAddImage.setOnClickListener {
            //检查权限
            if (ControlPermission.checkPermission(this)) {
                //如果用户已经授与了权限，则打开MediaStore app，让用户选择图片
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncherForSelectImage.launch(intent)
            } else {
                //依据手机版本，申请不同名字的权限
                if (Build.VERSION.SDK_INT >= 33) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        1
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
            }
        }
        //用户点击“保存”按钮，收藏图片到数据库
        addImageBinding.btnAdd.setOnClickListener {
            if (saveFlag) {
                addImageBinding.btnAdd.text = "正在保存……"
                addImageBinding.btnAdd.isEnabled = false
                //在IO线程中完成图片转字符串，以及保存到数据库中的所有任务
                MainScope().launch(Dispatchers.IO) {
                    val title = addImageBinding.edtAddTitle.text.toString()
                    val description = addImageBinding.edtAddDescription.text.toString()
                    val imageAsString = ConvertImage.convertToString(selectedImage)
                    if (imageAsString != null) {
                        myImagesViewModel.insert(MyImages(title, description, imageAsString))
                        saveFlag = false
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "出错了", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "请选择一张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //注册用户从MediaStore中选择图片后的回调函数
    fun registerActivityForSelectImage() {
        activityResultLauncherForSelectImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //获取用户选择的结果
                val resultCode = result.resultCode
                val imageData = result.data
                //用户确定选择了图片
                if (resultCode == RESULT_OK && imageData != null) {
                    val imageUri = imageData.data
                    //从URI中解码，得到Bitmap对象
                    imageUri?.let {
                        selectedImage = if (Build.VERSION.SDK_INT >= 28) {
                            val imageSource =
                                ImageDecoder.createSource(this.contentResolver, imageUri)
                            ImageDecoder.decodeBitmap(imageSource)
                        } else {
                            MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                        }
                        //显示在ImageView中
                        addImageBinding.imageViewAddImage.setImageBitmap(selectedImage)
                        saveFlag = true
                    }
                }
            }
    }

    //处理权限许可结果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //如果用户确实授与了相应的权限
            val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //启动MediaStore，让用户选择图片
            activityResultLauncherForSelectImage.launch(intent)
        }
    }
}