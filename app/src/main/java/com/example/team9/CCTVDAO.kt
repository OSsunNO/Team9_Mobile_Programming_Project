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
    @Query("SELECT * FROM CCTV WHERE latitude BETWEEN :latitude - 0.0090100236513120846942223223336 AND :latitude + 0.0090100236513120846942223223336 AND " +
            "longitude BETWEEN :longitude - 0.01131945191213841425798162852955 AND :longitude + 0.01131945191213841425798162852955")
    fun getNear(latitude: Double, longitude: Double): MutableList<CCTV>
//    latitude (distance*2)/(110941 + 111034)
//    longitude (distance*2) / (91290 + 85397)
//    latitude +- 0.00180200473026241693884446446672
//    +- 0.0090100236513120846942223223336 1km
//    longitude +- 0.00226389038242768285159632570591
//    +- 0.01131945191213841425798162852955 1km
}