package com.example.mynavigationdemo.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mynavigationdemo.databinding.ImageItemBinding
import com.example.mynavigationdemo.model.MyImages
import com.example.mynavigationdemo.util.ConvertImage
import com.example.mynavigationdemo.view.UpdateImageActivity

class MyImagesAdapter(val activity: Activity) :
    RecyclerView.Adapter<MyImagesAdapter.MyImagesViewHolder>() {

    var imageList: List<MyImages> = ArrayList()

    fun setImage(images: List<MyImages>) {
        imageList = images
        //通知RecyclerView刷新显示
        notifyDataSetChanged()
    }

    class MyImagesViewHolder(val itemBinding: ImageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesViewHolder {
        val view = ImageItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return MyImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    //从图片列表中选择对应图片，显示在RecyclerView的一行中
    override fun onBindViewHolder(holder: MyImagesViewHolder, position: Int) {
        val myImage = imageList[position]
        with(holder) {
            itemBinding.textViewTitle.text = myImage.imageTitle
            itemBinding.textViewDescription.text = myImage.imageDescription
            val imageAsBitmap = ConvertImage.convertToBitmap(myImage.imageAsString)
            itemBinding.imageView.setImageBitmap(imageAsBitmap)
            //点击图片本身时，显示“更新”的Activity，用于更新数据
            itemBinding.cardView.setOnClickListener {
                val intent = Intent(activity, UpdateImageActivity::class.java)
                intent.putExtra("id", myImage.imageId)
                activity.startActivity(intent)
            }
        }
    }

    fun returnItemGivenPosition(position: Int): MyImages {
        //获取指定位置的图片对象
        return imageList[position]
    }
}