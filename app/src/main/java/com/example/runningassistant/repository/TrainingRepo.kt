package com.example.runningassistant.repository

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.example.runningassistant.model.TrainingTableModel
import com.example.runningassistant.room.TrainingDatabase
import com.example.runningassistant.model.IntervalItem
import com.example.runningassistant.model.TrainingResultTableModel
import com.mapbox.geojson.Point
import java.util.*

object TrainingRepo {

    private var trainingDatabase: TrainingDatabase? = null

    private var allTrainingNames: LiveData<List<String>>? = null
    private var trainingDetails: LiveData<TrainingTableModel>? = null

    private var allTrainingResults: LiveData<List<TrainingResultTableModel>>? = null

    private fun initializeDB(context: Context): TrainingDatabase {
        return TrainingDatabase.getDatabaseClient(context)
    }

    suspend fun insertTraining(
        context: Context,
        title: String,
        trainingPlan: List<IntervalItem>,
        repeats: Int,
        warmUp: Boolean,
        coolDown: Boolean
    ) {
        trainingDatabase = initializeDB(context)

        val trainingDetails =
            TrainingTableModel(title, trainingPlan, repeats, warmUp, coolDown)
        trainingDatabase!!.trainingDao().insertTraining(trainingDetails)
    }

    fun getAllTrainingNames(context: Context): LiveData<List<String>>? {
        trainingDatabase = initializeDB(context)

        allTrainingNames = trainingDatabase!!.trainingDao().getAllTrainingNames()

        return allTrainingNames
    }

    fun getTrainingDetails(
        context: Context,
        title: String
    ): LiveData<TrainingTableModel>? {
        trainingDatabase = initializeDB(context)

        trainingDetails = trainingDatabase!!.trainingDao().getTrainingDetails(title)

        return trainingDetails
    }

    suspend fun updateTraining(context: Context, trainingTableModel: TrainingTableModel) {
        trainingDatabase = initializeDB(context)

        trainingDatabase!!.trainingDao().updateTraining(trainingTableModel)
    }

    suspend fun insertTrainingResult(
        context: Context,
        pointsList: List<Point>,
        totalDistance: Float,
        totalTime: Int,
        condition: Int,
        intervalEndIndexes: List<Int>,
        speedsList: List<Float>,
        currentDate: Date
    ) {
        trainingDatabase = initializeDB(context)

        val trainingResultTableModel =
            TrainingResultTableModel(
                pointsList,
                intervalEndIndexes,
                speedsList,
                totalDistance,
                totalTime,
                condition,
                currentDate
            )
        trainingDatabase!!.trainingDao().insertTrainingResult(trainingResultTableModel)
    }

    fun getAllTrainingResults(context: Context): LiveData<List<TrainingResultTableModel>>? {
        trainingDatabase = initializeDB(context)

        allTrainingResults = trainingDatabase!!.trainingDao().getAllTrainingResults()

        return allTrainingResults
    }
}