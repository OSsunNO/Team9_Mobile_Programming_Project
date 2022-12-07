package com.example.team9

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CCTVDAO {
    // convenient method for inserting newly generated data into the room database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cctv: CCTV)
    // SQL query for deleting all the instances in the room database
    @Query("DELETE FROM CCTV")
    fun deleteAll()
    // SQL query for getting all instances in the room database
    @Query("SELECT * FROM CCTV")
    fun getAll(): MutableList<CCTV>
//    @Query("SELECT * FROM CCTV WHERE latitude ")
//    fun
}