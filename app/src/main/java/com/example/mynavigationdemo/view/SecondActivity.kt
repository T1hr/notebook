package com.example.mynavigationdemo.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mynavigationdemo.R
import com.example.mynavigationdemo.adapter.MyImagesAdapter
import com.example.mynavigationdemo.databinding.ActivitySecondBinding
import com.example.mynavigationdemo.viewmodel.MyImagesViewModel

class SecondActivity : AppCompatActivity() {
    lateinit var myImagesViewModel: MyImagesViewModel
    lateinit var secondBinding: ActivitySecondBinding
    lateinit var myImagesAdapter: MyImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        getWindow().setBackgroundDrawableResource(R.drawable.bg1);
        //初始化视图绑定
        secondBinding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(secondBinding.root)
        //实例化ViewModel
        myImagesViewModel = ViewModelProvider(this)[MyImagesViewModel::class.java]

        //初始化RecyclerView
        secondBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        myImagesAdapter = MyImagesAdapter(this)
        secondBinding.recyclerView.adapter = myImagesAdapter

        //显示所有的列表
        myImagesViewModel.getAllImages().observe(this) { images ->
            myImagesAdapter.setImage(images)
        }

        secondBinding.floatingActionButton.setOnClickListener {
            //显示“收藏图片”Activity
            val intent = Intent(this, AddImageActivity::class.java)
            startActivity(intent)
        }

        //给RecyclerView添加手势支持
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }
            // 当用户用手指左右滑动时，删除指定的记录
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val imageItem = myImagesAdapter.returnItemGivenPosition(position)

                // 显示确认对话框
                AlertDialog.Builder(this@SecondActivity)
                    .setTitle("确认删除")
                    .setMessage("您确定要删除这项记录吗？")
                    .setPositiveButton("删除") { dialog, _ ->
                        myImagesViewModel.delete(imageItem)
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        // 取消删除，重新绑定视图
                        myImagesAdapter.notifyItemChanged(viewHolder.adapterPosition)
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }).attachToRecyclerView(secondBinding.recyclerView)
    }
}