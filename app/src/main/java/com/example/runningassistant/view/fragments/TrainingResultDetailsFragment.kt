package com.example.runningassistant.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentTrainingResultDetailsBinding
import com.example.runningassistant.model.TrainingResultTableModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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
import java.lang.StringBuilder
import java.util.*

class TrainingResultDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTrainingResultDetailsBinding

    private var trainingResult: TrainingResultTableModel? = null

    private val args: TrainingResultDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_training_result_details,
            null,
            false
        )

        trainingResult = if (args.trainingResult != null) {
            val gson = Gson()
            val type = object : TypeToken<TrainingResultTableModel>() {}.type
            gson.fromJson(args.trainingResult, type)
        } else {
            null
        }

        if (trainingResult != null) {
            setupMap()
            setupChart()
            setupViews()
        }

        return binding.root
    }

    private fun setupMap() {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        binding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        )
        //turning location list to point list
        if (trainingResult!!.PointsList.isEmpty()) {
            return
        }
        //centering map
        binding.mapView.getMapboxMap()
            .setCamera(CameraOptions.Builder().center(trainingResult!!.PointsList[0]).build())
        binding.mapView.gestures.focalPoint =
            binding.mapView.getMapboxMap().pixelForCoordinate(trainingResult!!.PointsList[0])
        //drawing path
        val annotationsApi = binding.mapView.annotations

        val polylineAnnotationManager = annotationsApi.createPolylineAnnotationManager()
        val polylineAnnotationOptions =
            PolylineAnnotationOptions().withPoints(trainingResult!!.PointsList)
                .withLineColor("#ee4e8b")
                .withLineWidth(5.0)

        polylineAnnotationManager.create(polylineAnnotationOptions)
        //drawing start and finish
        val circleAnnotationManager = annotationsApi.createCircleAnnotationManager()
        var circleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(trainingResult!!.PointsList[0])
            .withCircleRadius(8.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")

        circleAnnotationManager.create(circleAnnotationOptions)

        circleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(trainingResult!!.PointsList.last())
            .withCircleRadius(8.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")

        circleAnnotationManager.create(circleAnnotationOptions)
    }

    private fun setupViews() {
        val calendar = Calendar.getInstance()
        calendar.time = trainingResult!!.TrainingDate

        val dateStr = StringBuilder("")

        dateStr.append(calendar.get(Calendar.DAY_OF_MONTH))
        dateStr.append(" ")
        dateStr.append(
            calendar.getDisplayName(
                Calendar.MONTH,
                Calendar.LONG,
                Locale.getDefault()
            )
        )


        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..4, in 22..24 -> dateStr.append(" - Night")
            in 5..12 -> dateStr.append(" - Morning")
            in 13..17 -> dateStr.append(" - Day")
            in 18..21 -> dateStr.append("Evening")
        }

        binding.dateTextView.text = dateStr.toString()

        when (trainingResult!!.Condition) {
            TrainingResultTableModel.CONDITION_VERY_BAD -> binding.conditionImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.conditionImageView.context,
                    R.drawable.ic_baseline_sentiment_very_dissatisfied_24
                )
            )
            TrainingResultTableModel.CONDITION_BAD -> binding.conditionImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.conditionImageView.context,
                    R.drawable.ic_baseline_sentiment_dissatisfied_24
                )
            )
            TrainingResultTableModel.CONDITION_OK -> binding.conditionImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.conditionImageView.context,
                    R.drawable.ic_baseline_sentiment_satisfied_24
                )
            )
            TrainingResultTableModel.CONDITION_GOOD -> binding.conditionImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.conditionImageView.context,
                    R.drawable.ic_baseline_sentiment_satisfied_alt_24
                )
            )
            TrainingResultTableModel.CONDITION_VERY_GOOD -> binding.conditionImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.conditionImageView.context,
                    R.drawable.ic_baseline_sentiment_very_satisfied_24
                )
            )
        }
    }

    private fun setupChart() {
        //speed over time
        var entries = List(trainingResult!!.PointsList.size) { index ->
            Entry(
                trainingResult!!.TotalTime.toFloat() / trainingResult!!.SpeedsList.size * index,
                trainingResult!!.SpeedsList[index] / 1000 * 3600
            )
        }

        var dataSet = LineDataSet(entries, "Speed")

        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(false)
        dataSet.lineWidth = 3f
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.purple_500)
        dataSet.setDrawCircles(false)

        binding.speedLineChart.xAxis.labelRotationAngle = 0f
        binding.speedLineChart.data = LineData(dataSet)
        binding.speedLineChart.axisRight.isEnabled = false
        binding.speedLineChart.xAxis.axisMaximum = entries.last().x + 0.1f

        binding.speedLineChart.setTouchEnabled(true)
        binding.speedLineChart.setPinchZoom(true)

        binding.speedLineChart.description.text = "Speed graph over time"

        binding.speedLineChart.animateX(1800, Easing.EaseInExpo)

        //speed over distance
        entries = List(trainingResult!!.PointsList.size) { index ->
            Entry(
                trainingResult!!.TotalDistance / trainingResult!!.SpeedsList.size * index,
                trainingResult!!.SpeedsList[index] / 1000 * 3600
            )
        }

        dataSet = LineDataSet(entries, "Speed")

        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(false)
        dataSet.lineWidth = 3f
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.purple_500)
        dataSet.setDrawCircles(false)

        binding.speedDistanceLineChart.xAxis.labelRotationAngle = 0f
        binding.speedDistanceLineChart.data = LineData(dataSet)
        binding.speedDistanceLineChart.axisRight.isEnabled = false
        binding.speedDistanceLineChart.xAxis.axisMaximum = entries.last().x + 0.1f

        binding.speedDistanceLineChart.setTouchEnabled(true)
        binding.speedDistanceLineChart.setPinchZoom(true)

        binding.speedDistanceLineChart.description.text = "Speed graph over distance"

        binding.speedDistanceLineChart.animateX(1800, Easing.EaseInExpo)
    }

}