package com.example.mynavigationdemo.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mynavigationdemo.model.MyImages

@Database(entities = [MyImages::class], version = 1)
abstract class MyImagesDatabase : RoomDatabase() {
    abstract fun myImagesDao(): MyImagesDao
    //习惯做法，Database对象应该是Singleton并且线程安全的
    companion object {
        @Volatile
        private var instance: MyImagesDatabase? = null
        fun getDatabaseInstance(context: Context): MyImagesDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyImagesDatabase::class.java,
                    "my_album"
                ).build()
            }
            return instance as MyImagesDatabase
        }
    }
}