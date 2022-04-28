package com.example.runningassistant.view.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentMainBinding
import com.example.runningassistant.model.TrainingExecution
import com.example.runningassistant.model.TrainingTableModel
import com.example.runningassistant.model.IntervalItem
import com.example.runningassistant.viewmodel.TrainingViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import java.util.*


//make polyline global and add points
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val trainingViewModel: TrainingViewModel by activityViewModels()

    private var tts: TextToSpeech? = null
    private var isTtsInitFinished = false

    private var isVoiceTravelledDistance = false
    private var isVoicePassedTime = false
    private var isVoiceCurrentSpeed = false
    private var isVoiceAverageSpeed = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        trainingViewModel.updateChosenTrainingName(
            sharedPref?.getString(
                getString(R.string.preference_training_name_key),
                ""
            ) ?: ""
        )

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        binding.chooseAudioButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_audioCustomizationFragment)
        }

        binding.chooseTrainingButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_trainingSelectionFragment)
        }


        binding.startButton.setOnClickListener {
            trainingViewModel.getChosenTrainingDetails(requireContext())
                ?.observe(viewLifecycleOwner, object : Observer<TrainingTableModel> {
                    override fun onChanged(trainingModel: TrainingTableModel?) {
                        trainingViewModel.liveDataTrainingModel?.removeObserver(this)

                        val mapBoxNavigation = if (MapboxNavigationProvider.isCreated()) {
                            MapboxNavigationProvider.retrieve()
                        } else {
                            //temp token
                            val navigationOptions = NavigationOptions.Builder(requireContext())
                                .accessToken(getString(R.string.mapbox_access_token))
                                .build()
                            MapboxNavigationProvider.create(navigationOptions)
                        }
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            if (trainingModel != null) {
                                val updatePair = setupAudio()
                                trainingViewModel.startTraining(
                                    trainingModel,
                                    executionListener,
                                    updatePair.first,
                                    updatePair.second
                                )
                                mapBoxNavigation.startTripSession(true)
                                mapBoxNavigation.registerLocationObserver(locationObserver)
                                binding.startButton.visibility = View.INVISIBLE
                                binding.chooseTrainingButton.visibility = View.INVISIBLE
                                binding.chooseAudioButton.visibility = View.INVISIBLE

                            }

                        }
                    }
                })
        }

        onMapReady()

        return binding.root
    }

    private fun setupAudio(): Pair<Int, Int> {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            val isAudioEnabled =
                sharedPref.getBoolean(getString(R.string.preference_enable_audio_key), false)
            if (isAudioEnabled) {
                tts = TextToSpeech(requireContext(), ttsListener)

                isVoiceAverageSpeed =
                    sharedPref.getBoolean(getString(R.string.preference_average_speed_key), false)
                isVoiceTravelledDistance = sharedPref.getBoolean(
                    getString(R.string.preference_distance_travelled_key),
                    false
                )
                isVoicePassedTime =
                    sharedPref.getBoolean(getString(R.string.preference_time_passed_key), false)
                isVoiceCurrentSpeed =
                    sharedPref.getBoolean(getString(R.string.preference_current_speed_key), false)
                return Pair(
                    sharedPref.getInt(
                        getString(R.string.preference_distance_update_key),
                        0
                    ), sharedPref.getInt(getString(R.string.preference_time_update_key), 0)
                )
            }
        }
        return Pair(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.location.removeOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
        binding.mapView.location.removeOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)

        val mapBoxNavigation = if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            val navigationOptions = NavigationOptions.Builder(requireContext())
                .accessToken(getString(R.string.mapbox_access_token))
                .build()
            MapboxNavigationProvider.create(navigationOptions)
        }
        mapBoxNavigation.stopTripSession()
        mapBoxNavigation.unregisterLocationObserver(locationObserver)
        MapboxNavigationProvider.destroy()
    }

    private val ttsListener = TextToSpeech.OnInitListener { status ->
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(requireContext(), "The language is not supported", Toast.LENGTH_LONG)
            } else {
                isTtsInitFinished = true
            }
        }
    }

    //Map specific things
    private val points: MutableList<Point> = mutableListOf()

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        binding.mapView.gestures.focalPoint = binding.mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {

        }
    }

    private fun onMapReady() {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        binding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }
    }

    private fun setupGesturesListener() {
        binding.mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        binding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun addNewPoint(newPoint: Point) {
        points.add(newPoint)
        Log.i("MainFragment-points", "new point added to list:$newPoint")
        val annotationsApi = binding.mapView.annotations

        val polylineAnnotationManager = annotationsApi.createPolylineAnnotationManager()
        val polylineAnnotationOptions =
            PolylineAnnotationOptions().withPoints(points).withLineColor("#ee4e8b")
                .withLineWidth(5.0)

        polylineAnnotationManager.create(polylineAnnotationOptions)
    }

    private val locationObserver = object : LocationObserver {
        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
        }

        override fun onNewRawLocation(rawLocation: Location) {
            val point = Point.fromLngLat(rawLocation.longitude, rawLocation.latitude)
            addNewPoint(point)

            trainingViewModel.updateTrainingExecutionLocation(rawLocation)
        }
    }

    //Execution interaction
    private val executionListener = object : TrainingExecution.TrainingExecutionListener {
        override fun onTrainingIsOver(
            locationList: List<Location>,
            totalDistance: Float,
            totalTime: Int
        ) {
            if (isTtsInitFinished) {
                tts!!.speak("Training is over", TextToSpeech.QUEUE_ADD, null, "")
            }
            val mapBoxNavigation = if (MapboxNavigationProvider.isCreated()) {
                MapboxNavigationProvider.retrieve()
            } else {
                val navigationOptions = NavigationOptions.Builder(requireContext())
                    .accessToken(getString(R.string.mapbox_access_token))
                    .build()
                MapboxNavigationProvider.create(navigationOptions)
            }
            mapBoxNavigation.stopTripSession()
            mapBoxNavigation.unregisterLocationObserver(locationObserver)
            binding.startButton.visibility = View.VISIBLE
            binding.chooseTrainingButton.visibility = View.VISIBLE
            binding.chooseAudioButton.visibility = View.VISIBLE
            val points: List<Point> = List(locationList.size) { index ->
                Point.fromLngLat(locationList[index].longitude, locationList[index].latitude)
            }

            val gson = Gson()
            val type = object : TypeToken<List<Point>>() {}.type
            val pointsString = gson.toJson(points, type)

            val action =
                MainFragmentDirections.actionMainFragmentToTrainingSaveFragment(
                    totalDistance,
                    totalTime,
                    pointsString,
                    if (totalTime != 0) totalDistance / totalTime else 0F
                )
            findNavController().navigate(action)
        }

        override fun onIntervalStarted(
            isIntervalDistance: Boolean,
            goal: Float,
            intervalType: Int
        ) {
            if (isTtsInitFinished) {
                val str = StringBuilder("Interval started.")
                when (intervalType) {
                    IntervalItem.INTERVAL_TYPE_SLOW -> str.append("Slow speed")
                    IntervalItem.INTERVAL_TYPE_NORMAL -> str.append("Normal speed")
                    IntervalItem.INTERVAL_TYPE_FAST -> str.append("Fast speed")
                    else -> str.append("Speed not specified")
                }
                if (isIntervalDistance) {
                    str.append("Distance: ${(goal / 1000).toInt()} kilometers and ${goal % 1000} meters")
                } else {
                    str.append("Time: ${(goal / 60).toInt()} minutes and  ${goal % 60} seconds")
                }
                tts!!.speak(str.toString(), TextToSpeech.QUEUE_ADD, null, "")
            }
            Toast.makeText(
                requireContext(),
                "Started interval on distance:$isIntervalDistance with type:$intervalType\n Goal: $goal",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onUpdateTimeCame(
            timePassed: Int,
            currentSpeed: Float,
            averageIntervalSpeed: Float,
            totalDistanceCovered: Float
        ) {
            if (isTtsInitFinished) {
                val str = StringBuilder("")

                if (isVoicePassedTime) {
                    str.append("${timePassed / 60} minutes and ${timePassed % 60} seconds passed.")
                }
                if (isVoiceTravelledDistance) {
                    str.append("${(totalDistanceCovered / 1000).toInt()} kilometers and ${(totalDistanceCovered % 1000).toInt()} meters travelled.")
                }
                if (isVoiceCurrentSpeed) {
                    str.append("Current speed is $currentSpeed")
                }
                if (isVoiceAverageSpeed) {
                    str.append("Average interval speed is $averageIntervalSpeed")
                }

                if (str.toString() != "") {
                    tts!!.speak(str.toString(), TextToSpeech.QUEUE_ADD, null, "")
                }
            }
            Toast.makeText(
                requireContext(),
                "Time passed: $timePassed\nCurrent speed:$currentSpeed\nAvg interval speed:$averageIntervalSpeed\nTotal distance covered:$totalDistanceCovered",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}