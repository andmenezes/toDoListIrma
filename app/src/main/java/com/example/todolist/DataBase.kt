package com.example.todolist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Notes::class], version = 1, exportSchema = false)
abstract class DataBase : RoomDatabase() {

    abstract fun dao(): Dao

    companion object{
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getDatabase(context: Context):DataBase{
            val instance = INSTANCE
            if (instance!=null){
                return instance
            }

            synchronized(this){
                val instance = databaseBuilder(
                    context.applicationContext, DataBase::class.java, "notes"
                ).build()

                INSTANCE = instance
                return instance
            }
        }

    }
}