package com.example.runningassistant.view.fragments

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentTrainingSaveBinding
import com.example.runningassistant.model.TrainingResultTableModel
import com.example.runningassistant.viewmodel.TrainingViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import java.util.*

class TrainingSaveFragment : Fragment() {
    private lateinit var binding: FragmentTrainingSaveBinding

    private val trainingViewModel: TrainingViewModel by activityViewModels()

    private var totalDistance: Float = 0F
    private var averageSpeed: Float = 0F
    private var totalTime = 0
    private var pointsList: List<Point> = emptyList()
    private var intervalEndIndexes: List<Int> = emptyList()
    private var speedsList: List<Float> = emptyList()
    private var currentTime: Date = Date()


    private val args: TrainingSaveFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training_save, null, false)
        binding.chosenCondition = 2

        totalDistance = args.totalDistance
        binding.totalDistanceTexView.text =
            String.format(Locale.getDefault(), "%.2f", totalDistance / 1000)
        totalTime = args.totalTime
        binding.totalTimeTexView.text =
            String.format(Locale.getDefault(), "%d:%d", totalTime / 60, totalTime % 60)
        averageSpeed = args.averageSpeed / 1000 * 3600
        binding.averageSpeedTextView.text = String.format(Locale.getDefault(), "%.2f", averageSpeed)
        pointsList = if (args.pointsList != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Point>>() {}.type
            gson.fromJson(args.pointsList, type)
        } else {
            emptyList()
        }
        intervalEndIndexes = if (args.intervalEndIndexes != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(args.intervalEndIndexes, type)
        } else {
            emptyList()
        }
        speedsList = if (args.speedsList != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Float>>() {}.type
            gson.fromJson(args.speedsList, type)
        } else {
            emptyList()
        }
        currentTime = Calendar.getInstance().time

        setupMap()

        setupListeners()
        return binding.root
    }

    private fun setupMap() {
        binding.finishedTrainingMapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        binding.finishedTrainingMapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        )
        //turning location list to point list
        if (pointsList.isEmpty()) {
            return
        }
        //centering map
        binding.finishedTrainingMapView.getMapboxMap()
            .setCamera(CameraOptions.Builder().center(pointsList[0]).build())
        binding.finishedTrainingMapView.gestures.focalPoint =
            binding.finishedTrainingMapView.getMapboxMap().pixelForCoordinate(pointsList[0])
        //drawing path
        val annotationsApi = binding.finishedTrainingMapView.annotations

        val polylineAnnotationManager = annotationsApi.createPolylineAnnotationManager()
        val polylineAnnotationOptions =
            PolylineAnnotationOptions().withPoints(pointsList).withLineColor("#ee4e8b")
                .withLineWidth(5.0)

        polylineAnnotationManager.create(polylineAnnotationOptions)
        //drawing start and finish
        val circleAnnotationManager = annotationsApi.createCircleAnnotationManager()
        var circleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(pointsList[0])
            .withCircleRadius(8.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")

        circleAnnotationManager.create(circleAnnotationOptions)

        circleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(pointsList.last())
            .withCircleRadius(8.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")

        circleAnnotationManager.create(circleAnnotationOptions)
    }

    private fun setupListeners() {
        binding.conditionVeryBadButton.setOnClickListener {
            binding.chosenCondition = TrainingResultTableModel.CONDITION_VERY_BAD
        }

        binding.conditionBadButton.setOnClickListener {
            binding.chosenCondition = TrainingResultTableModel.CONDITION_BAD
        }

        binding.conditionOkButton.setOnClickListener {
            binding.chosenCondition = TrainingResultTableModel.CONDITION_OK
        }

        binding.conditionGoodButton.setOnClickListener {
            binding.chosenCondition = TrainingResultTableModel.CONDITION_GOOD
        }

        binding.conditionVeryGoodButton.setOnClickListener {
            binding.chosenCondition = TrainingResultTableModel.CONDITION_VERY_GOOD
        }

        binding.saveTrainingResultButton.setOnClickListener {
            trainingViewModel.insertTrainingResult(
                requireContext(),
                pointsList,
                totalDistance,
                totalTime,
                if (binding.chosenCondition == null) 0 else binding.chosenCondition!!,
                intervalEndIndexes,
                speedsList,
                currentTime
            )
            findNavController().navigate(R.id.action_trainingSaveFragment_to_mainFragment)
        }
    }

}