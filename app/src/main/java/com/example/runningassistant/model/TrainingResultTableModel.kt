package com.example.runningassistant.model

import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mapbox.geojson.Point
import java.util.*

@Entity(tableName = "TrainingResult")
class TrainingResultTableModel(
    @ColumnInfo(name = "Points_list")
    var PointsList: List<Point>,

    @ColumnInfo(name = "Interval_end_indices")
    var IntervalEndIndices: List<Int>,

    @ColumnInfo(name = "Speeds_List")
    var SpeedsList: List<Float>,

    @ColumnInfo(name = "Total_distance")
    var TotalDistance: Float,

    @ColumnInfo(name = "Total_time")
    var TotalTime: Int,

    @ColumnInfo(name = "Condition")
    var Condition: Int,

    @ColumnInfo(name = "Training_Date")
    var TrainingDate: Date
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var Id: Int? = null

    companion object {
        const val CONDITION_VERY_BAD = 0
        const val CONDITION_BAD = 1
        const val CONDITION_OK = 2
        const val CONDITION_GOOD = 3
        const val CONDITION_VERY_GOOD = 4
    }
}