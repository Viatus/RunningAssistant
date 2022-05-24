package com.example.runningassistant.model

import android.location.Location
import android.util.Log
import kotlinx.coroutines.*

class TrainingExecution(
    private var executedTraining: TrainingTableModel,
    private val trainingExecutionListener: TrainingExecutionListener,
    private val coroutineScope: CoroutineScope,
    private val distanceBetweenReports: Int
) {

    interface TrainingExecutionListener {
        fun onTrainingIsOver(locationList: List<Location>, totalDistance: Float, totalTime: Int, intervalEndIndexes: List<Int>)
        fun onIntervalStarted(isIntervalDistance: Boolean, goal: Float, intervalType: Int)
        fun onUpdateTimeCame(
            timePassed: Int,
            currentSpeed: Float,
            averageIntervalSpeed: Float,
            totalDistanceCovered: Float
        )
    }


    private var currentTimeSeconds = 0
    private var currentIntervalStartTime = 0
    private var currentInterval = -1
    private var repeatsDone = 0
    private var currentIntervalDistanceLeft = Float.MAX_VALUE
    private var isCurrentIntervalIsDistance = false
    private var totalDistanceTravelled = 0F
    private var lastTrackedSpeed = 0F
    private var isTrainingOngoing = true

    private var locationsList = mutableListOf<Location>()
    private var intervalLastPointIndexes = mutableListOf<Int>()

    private var distanceBetweenReportsModifier = 1


    //Location updates handling

    fun onLocationUpdate(rawLocation: Location) {
        coroutineScope.launch {
            //Initial checkpoint
            if (currentInterval == -3) {
                if (!executedTraining.WarmUp) {
                    currentInterval = -1
                } else {
                    currentInterval = -2
                    delay(5 * 60 * 1000)
                    currentInterval = -1
                    return@launch
                }
            }

            if (currentInterval == -1) {
                advanceInterval()
            }

            if (locationsList.isEmpty()) {
                locationsList.add(rawLocation)
            } else {
                val oldLocation = locationsList.last()
                val distanceCompleted = rawLocation.distanceTo(oldLocation)
                totalDistanceTravelled += distanceCompleted
                locationsList.add(rawLocation)
                lastTrackedSpeed = rawLocation.speed
                if (isCurrentIntervalIsDistance && currentInterval >= -1) {
                    Log.i("TrainingExecution", "currentIntervalDistanceLeft=$currentIntervalDistanceLeft; distanceCompleted=$distanceCompleted")
                    currentIntervalDistanceLeft -= distanceCompleted
                    if (currentIntervalDistanceLeft <= 0) {
                        advanceInterval()
                    }
                }
                if (distanceBetweenReports != 0) {
                    if (totalDistanceTravelled >= distanceBetweenReports * distanceBetweenReportsModifier) {
                        distanceBetweenReportsModifier++
                        val avgSpeed =
                            calculateAvgSpeed(currentTimeSeconds - currentIntervalStartTime)
                        withContext(Dispatchers.Main) {
                            trainingExecutionListener.onUpdateTimeCame(
                                currentTimeSeconds,
                                lastTrackedSpeed,
                                avgSpeed,
                                totalDistanceTravelled
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun advanceInterval() {
        Log.i("TrainingExecution", "Executed interval $currentInterval")
        intervalLastPointIndexes.add(locationsList.lastIndex)
        if (currentInterval >= executedTraining.TrainingPlan.size - 1) {
            repeatsDone++
            if (repeatsDone >= executedTraining.Repeats + 1) {
                if (executedTraining.Cooldown) {
                    delay(5 * 60 * 1000)
                    intervalLastPointIndexes.add(locationsList.lastIndex)
                }
                Log.i("TrainingExecution", "Finished training execution")
                isTrainingOngoing = false
                withContext(Dispatchers.Main) {
                    trainingExecutionListener.onTrainingIsOver(locationsList, totalDistanceTravelled, currentTimeSeconds, intervalLastPointIndexes)
                }
                return
            } else {
                currentInterval = -1
            }
        }
        currentInterval++
        if (executedTraining.TrainingPlan[currentInterval].intervalMeasurementType == IntervalItem.INTERVAL_MEASUREMENT_DISTANCE) {
            currentIntervalDistanceLeft = executedTraining.TrainingPlan[currentInterval].goal
            isCurrentIntervalIsDistance = true
            Log.i(
                "TrainingExecution",
                "Now executing interval $currentInterval with goal of $currentIntervalDistanceLeft meters"
            )
            withContext(Dispatchers.Main) {
                trainingExecutionListener.onIntervalStarted(
                    isCurrentIntervalIsDistance,
                    currentIntervalDistanceLeft,
                    executedTraining.TrainingPlan[currentInterval].intervalType
                )
            }
        } else if (executedTraining.TrainingPlan[currentInterval].intervalMeasurementType == IntervalItem.INTERVAL_MEASUREMENT_TIME) {
            val intervalTime = executedTraining.TrainingPlan[currentInterval].goal
            isCurrentIntervalIsDistance = false
            Log.i(
                "TrainingExecution",
                "Now executing interval $currentInterval with goal of $intervalTime seconds"
            )
            withContext(Dispatchers.Main) {
                trainingExecutionListener.onIntervalStarted(
                    isCurrentIntervalIsDistance,
                    intervalTime,
                    executedTraining.TrainingPlan[currentInterval].intervalType
                )
            }
            delay(intervalTime.toLong() * 1000)
            advanceInterval()
        }
    }

    fun setUpTimers(secondsBetweenReports: Int?) {
        coroutineScope.launch {
            while (isTrainingOngoing) {
                delay(1000)
                currentTimeSeconds++
                if (secondsBetweenReports != null) {
                    if (secondsBetweenReports != 0) {
                        if (currentTimeSeconds % secondsBetweenReports == 0) {
                            val avgSpeed =
                                calculateAvgSpeed(currentTimeSeconds - currentIntervalStartTime)
                            withContext(Dispatchers.Main) {
                                trainingExecutionListener.onUpdateTimeCame(
                                    currentTimeSeconds,
                                    lastTrackedSpeed,
                                    avgSpeed,
                                    totalDistanceTravelled
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun calculateAvgSpeed(time: Int): Float {
        var distanceCovered = 0F
        if (intervalLastPointIndexes.isEmpty()) {
            return 0F
        }
        var i = intervalLastPointIndexes.last() + 2
        val tempLocationList = locationsList
        val locationsListSize = locationsList.size
        while (i < locationsListSize) {
            distanceCovered += tempLocationList[i - 1].distanceTo(tempLocationList[i])
            i++
        }
        return distanceCovered / time
    }
}