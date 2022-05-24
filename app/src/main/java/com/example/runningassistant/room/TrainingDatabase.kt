package com.example.runningassistant.room

import android.content.Context
import android.location.Location
import androidx.room.*
import com.example.runningassistant.model.TrainingTableModel
import com.example.runningassistant.model.IntervalItem
import com.example.runningassistant.model.TrainingResultTableModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.Point
import java.util.*


@Database(
    entities = [TrainingTableModel::class, TrainingResultTableModel::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): DAOAccess

    companion object {

        @Volatile
        private var INSTANCE: TrainingDatabase? = null

        fun getDatabaseClient(context: Context): TrainingDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, TrainingDatabase::class.java, "TRAINING_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}

class Converters {
    @TypeConverter
    fun fromPairList(value: List<IntervalItem>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<IntervalItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPairList(value: String): List<IntervalItem> {
        val gson = Gson()
        val type = object : TypeToken<List<IntervalItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromPointList(value: List<Point>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Point>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPointList(value: String): List<Point> {
        val gson = Gson()
        val type = object : TypeToken<List<Point>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromTimeStamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(value: Date): Long {
        return value.time
    }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val gson = Gson()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Float>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        val gson = Gson()
        val type = object : TypeToken<List<Float>>() {}.type
        return gson.fromJson(value, type)
    }
}
