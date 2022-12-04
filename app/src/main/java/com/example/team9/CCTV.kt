package com.example.team9
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CCTV (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var address: String,
    var cameraNum: String,
    var latitude: String,
    var longitude: String,
)



