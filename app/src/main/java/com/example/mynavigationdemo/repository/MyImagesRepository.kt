package com.example.mynavigationdemo.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.mynavigationdemo.model.MyImages
import com.example.mynavigationdemo.room.MyImagesDao
import com.example.mynavigationdemo.room.MyImagesDatabase

//完成图片对象的CRUD功能
class MyImagesRepository(application: Application) {
    var myImagesDao: MyImagesDao
    var imageList: LiveData<List<MyImages>>
    init {
        val database = MyImagesDatabase.getDatabaseInstance(application)
        myImagesDao = database.myImagesDao()
        imageList = myImagesDao.getAllImages()
    }
    suspend fun insert(myImages: MyImages) {
        myImagesDao.insert(myImages)
    }
    suspend fun update(myImages: MyImages) {
        myImagesDao.update(myImages)
    }
    suspend fun delete(myImages: MyImages) {
        myImagesDao.delete(myImages)
    }
    //返回LiveData，供Activity进行“观察”
    fun getAllImages():LiveData<List<MyImages>>{
        return imageList
    }
    suspend fun getItemById(id:Int): MyImages {
        return myImagesDao.getItemById(id)
    }
}