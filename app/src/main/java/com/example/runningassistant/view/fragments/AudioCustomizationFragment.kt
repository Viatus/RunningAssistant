package com.example.runningassistant.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentAudioCustomizationBinding

class AudioCustomizationFragment : Fragment() {

    private lateinit var binding: FragmentAudioCustomizationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_audio_customization,
            container,
            false
        )

        binding.enableAudioSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                binding.distanceUpdateLabel.visibility = View.GONE
                binding.distanceUpdateEditText.visibility = View.GONE

                binding.timeUpdateLabel.visibility = View.GONE
                binding.timeUpdateEditText.visibility = View.GONE

                binding.distanceTravelledCheckbox.visibility = View.GONE
                binding.timePassedCheckbox.visibility = View.GONE
                binding.currentSpeedCheckbox.visibility = View.GONE
                binding.intervalSpeedCheckbox.visibility = View.GONE
            } else {
                binding.distanceUpdateLabel.visibility = View.VISIBLE
                binding.distanceUpdateEditText.visibility = View.VISIBLE

                binding.timeUpdateLabel.visibility = View.VISIBLE
                binding.timeUpdateEditText.visibility = View.VISIBLE

                binding.distanceTravelledCheckbox.visibility = View.VISIBLE
                binding.timePassedCheckbox.visibility = View.VISIBLE
                binding.currentSpeedCheckbox.visibility = View.VISIBLE
                binding.intervalSpeedCheckbox.visibility = View.VISIBLE
            }
        }

        binding.saveAudioParamsButton.setOnClickListener {
            saveAudioParams()
            findNavController().navigate(R.id.action_audioCustomizationFragment_to_mainFragment)
        }

        loadAudioParams()

        return binding.root
    }

    private fun saveAudioParams() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            with(sharedPref.edit()) {
                putBoolean(
                    getString(R.string.preference_enable_audio_key),
                    binding.enableAudioSwitch.isChecked
                )
                if (binding.enableAudioSwitch.isChecked) {
                    putInt(
                        getString(R.string.preference_distance_update_key),
                        binding.distanceUpdateEditText.text.toString().toInt()
                    )
                    putInt(
                        getString(R.string.preference_time_update_key),
                        binding.timeUpdateEditText.text.toString().toInt()
                    )

                    putBoolean(
                        getString(R.string.preference_distance_travelled_key),
                        binding.distanceTravelledCheckbox.isChecked
                    )
                    putBoolean(
                        getString(R.string.preference_time_passed_key),
                        binding.timePassedCheckbox.isChecked
                    )
                    putBoolean(
                        getString(R.string.preference_current_speed_key),
                        binding.currentSpeedCheckbox.isChecked
                    )
                    putBoolean(
                        getString(R.string.preference_average_speed_key),
                        binding.intervalSpeedCheckbox.isChecked
                    )
                }
                apply()
            }
        }
    }

    private fun loadAudioParams() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            val isAudioEnabled =
                sharedPref.getBoolean(getString(R.string.preference_enable_audio_key), false)
            if (isAudioEnabled) {
                binding.enableAudioSwitch.isChecked = true

                binding.distanceUpdateEditText.setText(
                    sharedPref.getInt(
                        getString(R.string.preference_distance_update_key),
                        0
                    ).toString()
                )
                binding.timeUpdateEditText.setText(
                    sharedPref.getInt(
                        getString(R.string.preference_time_update_key),
                        0
                    ).toString()
                )

                binding.distanceTravelledCheckbox.isChecked = sharedPref.getBoolean(
                    getString(R.string.preference_distance_travelled_key),
                    false
                )
                binding.timePassedCheckbox.isChecked =
                    sharedPref.getBoolean(getString(R.string.preference_time_passed_key), false)
                binding.currentSpeedCheckbox.isChecked =
                    sharedPref.getBoolean(getString(R.string.preference_current_speed_key), false)
                binding.intervalSpeedCheckbox.isChecked =
                    sharedPref.getBoolean(getString(R.string.preference_average_speed_key), false)
            } else {
                binding.enableAudioSwitch.isChecked = false
            }
        } else {
            binding.enableAudioSwitch.isChecked = false
        }
    }
}