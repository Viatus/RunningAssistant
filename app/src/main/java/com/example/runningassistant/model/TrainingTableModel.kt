package com.example.runningassistant.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Training")
data class TrainingTableModel(

    @ColumnInfo(name = "Title")
    var Title: String,

    @ColumnInfo(name = "TrainingPlan")
    var TrainingPlan: List<IntervalItem>,

    @ColumnInfo(name = "Repeats")
    var Repeats: Int,

    @ColumnInfo(name = "WarmUp")
    var WarmUp: Boolean,

    @ColumnInfo(name = "Cooldown")
    var Cooldown: Boolean,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var Id: Int? = null
}