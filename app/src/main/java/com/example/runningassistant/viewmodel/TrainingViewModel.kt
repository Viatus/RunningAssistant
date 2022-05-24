package com.example.runningassistant.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningassistant.model.*
import com.example.runningassistant.repository.RetrofitRepo
import com.example.runningassistant.repository.TrainingRepo
import com.mapbox.geojson.Point
import kotlinx.coroutines.*
import java.util.*

class TrainingViewModel : ViewModel() {
    var liveDataAllTrainingNames: LiveData<List<String>>? = null
    var liveDataTrainingModel: LiveData<TrainingTableModel>? = null
    var liveDataAllTrainingResults: LiveData<List<TrainingResultTableModel>>? = null

    private var chosenTrainingName: String = ""
    private var trainingExecution: TrainingExecution? = null

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun insertTraining(
        context: Context,
        title: String,
        trainingPlan: List<IntervalItem>,
        repeats: Int,
        warmUp: Boolean,
        coolDown: Boolean
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            TrainingRepo.insertTraining(context, title, trainingPlan, repeats, warmUp, coolDown)
        }
    }

    fun getAllTrainingNames(context: Context): LiveData<List<String>>? {
        liveDataAllTrainingNames = TrainingRepo.getAllTrainingNames(context)

        return liveDataAllTrainingNames
    }

    fun getTrainingDetails(context: Context, title: String): LiveData<TrainingTableModel>? {
        liveDataTrainingModel = TrainingRepo.getTrainingDetails(context, title)

        return liveDataTrainingModel
    }

    fun getChosenTrainingDetails(context: Context): LiveData<TrainingTableModel>? {
        return getTrainingDetails(context, chosenTrainingName)
    }

    fun updateTrainingModel(context: Context, trainingTableModel: TrainingTableModel) {
        CoroutineScope(Dispatchers.IO).launch {
            TrainingRepo.updateTraining(context, trainingTableModel)
        }
    }

    fun insertTrainingResult(
        context: Context,
        pointsList: List<Point>,
        totalDistance: Float,
        totalTime: Int,
        condition: Int,
        intervalEndIndexes: List<Int>,
        speedsList: List<Float>,
        currentDate: Date
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            TrainingRepo.insertTrainingResult(
                context,
                pointsList,
                totalDistance,
                totalTime,
                condition,
                intervalEndIndexes,
                speedsList,
                currentDate
            )
        }
    }

    fun getAllTrainingResults(context: Context): LiveData<List<TrainingResultTableModel>>? {
        liveDataAllTrainingResults = TrainingRepo.getAllTrainingResults(context)
        return liveDataAllTrainingResults
    }


    fun updateChosenTrainingName(name: String) {
        chosenTrainingName = name
    }

    fun startTraining(
        trainingTableModel: TrainingTableModel,
        trainingExecutionListener: TrainingExecution.TrainingExecutionListener,
        distanceBetweenReports: Int,
        timeBetweenReports: Int
    ) {
        trainingExecution = TrainingExecution(
            trainingTableModel,
            trainingExecutionListener,
            scope,
            distanceBetweenReports
        )
        trainingExecution?.setUpTimers(timeBetweenReports)
    }

    fun updateTrainingExecutionLocation(newLocation: Location) {
        trainingExecution?.onLocationUpdate(newLocation)
    }


    override fun onCleared() {
        super.onCleared()
        scope.coroutineContext.cancelChildren()
    }

    //weather staff
    val liveDataErrorMessage = MutableLiveData<String>()
    val liveDataCurrentWeather = MutableLiveData<CurrentWeatherModel>()

    fun getCurrentWeather(longitude: Float, latitude: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitRepo.getCurrentWeather(longitude, latitude)
            if (response.isSuccessful) {
                liveDataCurrentWeather.postValue(response.body()?.currentWeather)
            }
        }
    }

}