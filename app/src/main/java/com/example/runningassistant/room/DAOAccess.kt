package com.example.runningassistant.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.runningassistant.model.TrainingResultTableModel
import com.example.runningassistant.model.TrainingTableModel

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(trainingTableModel: TrainingTableModel)

    @Query("SELECT * FROM Training WHERE Title =:title ")
    fun getTrainingDetails(title: String): LiveData<TrainingTableModel>

    @Query("SELECT Title FROM Training")
    fun getAllTrainingNames(): LiveData<List<String>>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTraining(trainingTableModel: TrainingTableModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingResult(trainingResultTableModel: TrainingResultTableModel)

    @Query("SELECT * FROM TrainingResult ORDER BY Id DESC")
    fun getAllTrainingResults(): LiveData<List<TrainingResultTableModel>>
}