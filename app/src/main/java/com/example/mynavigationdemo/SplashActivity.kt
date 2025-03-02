package com.example.mynavigationdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.animation.AnimationUtils
import com.example.mynavigationdemo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    //这里启用了视图绑定
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //应用动画
        binding.SplashImageView.animation =
            AnimationUtils.loadAnimation(this, R.anim.image_animation)

        //使用Handler跨线程更新UI控件，延迟5秒，启动MainActivity，同时关闭自己
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                //显示主屏幕
                val intent = Intent(this@SplashActivity,
                    MainActivity::class.java)
                startActivity(intent)
                //关闭自己
                finish()
            }
        }, 2000)
    }
}