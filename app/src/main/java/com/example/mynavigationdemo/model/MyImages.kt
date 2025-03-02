package com.example.mynavigationdemo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//此类封装了图片信息，这里，将图片本身进行base64编码
//转换为字符串，以便保存到SQLite数据库中
@Entity(tableName = "my_images")
class MyImages(
    val imageTitle: String,
    val imageDescription: String,
    val imageAsString: String
) {
    //让SQLite数据库自动生成id值
    @PrimaryKey(autoGenerate = true)
    var imageId = 0;
}