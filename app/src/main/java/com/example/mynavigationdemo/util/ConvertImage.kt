package com.example.mynavigationdemo.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

//封装图片二进制数据与Base64编码字符串之间的相互转换功能
class ConvertImage {
    companion object {

        //将Base64编码的字符串，解析为Bitmap对象
        fun convertToBitmap(imageAsString: String): Bitmap {
            //将Base64解码，得到字节数组
            val byteArrayAsDecodedString = Base64.decode(imageAsString, Base64.DEFAULT)
            //对字节数组进行解码，得到Bitmap对象
            val bitmap = BitmapFactory.decodeByteArray(
                byteArrayAsDecodedString,
                0,
                byteArrayAsDecodedString.size
            )
            return bitmap
        }

        //太大的图片，生成的Base64编码字符串可能很大，超出SQLite数据库字段存储容量的上限
        //因此，要适当降低分辨率，以减少生成的字符串长度
        fun resizeImage(bitmap: Bitmap, coefficient: Double): String? {
            //按照指定的因数，对图片进行缩放，调整其分辨率，得到一个“较小”的Bitmap对象
            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * coefficient).toInt(),
                (bitmap.height * coefficient).toInt(),
                true
            )
            //按照PNG格式对图片进行压缩
            val newStream = ByteArrayOutputStream();
            val resultCompress = resizedBitmap.compress(Bitmap.CompressFormat.PNG,
                100, newStream)
            //压缩成功，则对二进制数据进行Base64编码
            if (resultCompress) {
                val newByteArray = newStream.toByteArray()
                return Base64.encodeToString(newByteArray, Base64.DEFAULT)
            }
            //压缩不成功，返回null
            return null
        }

        //将一个Bitmap对象转换为Base64编码的字符串
        fun convertToString(bitmap: Bitmap): String? {
            val stream = ByteArrayOutputStream()
            val resultCompress = bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            if (resultCompress) {
                val byteArray = stream.toByteArray()
                //检查图片文件大小，给与不同的缩放比率，得到Base64编码
                val imageAsString = if (byteArray.size > 200_0000) {
                    resizeImage(bitmap, 0.1)
                } else if (byteArray.size in 100_0000..200_0000) {
                    resizeImage(bitmap, 0.5)
                } else {
                    Base64.encodeToString(byteArray, Base64.DEFAULT)
                }
                return imageAsString
            }
            return null
        }
    }
}