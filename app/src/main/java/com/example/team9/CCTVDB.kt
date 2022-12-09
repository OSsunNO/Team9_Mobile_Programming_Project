package com.example.team9

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CCTV::class], version=2)
abstract class CCTVDB: RoomDatabase() {
    abstract fun cctvDAO():CCTVDAO

    companion object{
        @Volatile
        private var INSTANCE: CCTVDB? = null
        fun getInstance(context: Context): CCTVDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CCTVDB::class.java,
                    "lotto_database"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}