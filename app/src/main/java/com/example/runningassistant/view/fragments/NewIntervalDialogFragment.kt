package com.example.runningassistant.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentNewIntervalDialogBinding
import com.example.runningassistant.model.IntervalItem.Companion.INTERVAL_MEASUREMENT_DISTANCE
import com.example.runningassistant.model.IntervalItem.Companion.INTERVAL_MEASUREMENT_TIME
import com.example.runningassistant.model.IntervalItem.Companion.INTERVAL_TYPE_FAST
import com.example.runningassistant.model.IntervalItem.Companion.INTERVAL_TYPE_NORMAL
import com.example.runningassistant.model.IntervalItem.Companion.INTERVAL_TYPE_SLOW
import java.lang.IllegalStateException

class NewIntervalDialogFragment : DialogFragment() {


    private lateinit var binding: FragmentNewIntervalDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_interval_dialog,
                null,
                false
            )

            setUpElements()

            builder.setView(binding.root).setPositiveButton(
                R.string.save_text
            ) { dialog, _ ->
                val bundle = Bundle()
                val interval = listOf(
                    when (binding.intervalTypeSpinner.getItemAtPosition(binding.intervalTypeSpinner.selectedItemPosition)) {
                        resources.getStringArray(R.array.interval_types)[INTERVAL_TYPE_SLOW] -> {
                            INTERVAL_TYPE_SLOW
                        }
                        resources.getStringArray(R.array.interval_types)[INTERVAL_TYPE_NORMAL] -> {
                            INTERVAL_TYPE_NORMAL
                        }
                        resources.getStringArray(R.array.interval_types)[INTERVAL_TYPE_FAST] -> {
                            INTERVAL_TYPE_FAST
                        }
                        else -> {
                            INTERVAL_TYPE_SLOW
                        }
                    },
                    when (binding.intervalMesTypeSpinner.getItemAtPosition(binding.intervalMesTypeSpinner.selectedItemPosition)) {
                        resources.getStringArray(R.array.interval_mes_types)[INTERVAL_MEASUREMENT_DISTANCE] -> {
                            INTERVAL_MEASUREMENT_DISTANCE
                        }
                        resources.getStringArray(R.array.interval_mes_types)[INTERVAL_MEASUREMENT_TIME] -> {
                            INTERVAL_MEASUREMENT_TIME
                        }
                        else -> {
                            INTERVAL_MEASUREMENT_TIME
                        }
                    },
                    if (binding.intervalGoal.text.toString()
                            .toIntOrNull() == null
                    ) 0 else binding.intervalGoal.text.toString().toInt()
                )
                bundle.putIntArray(INTERVAL_BUNDLE_KEY, interval.toIntArray())
                setFragmentResult(INTERVAL_REQUEST_KEY, bundle)
                dialog?.cancel()
            }.setNegativeButton(
                R.string.cancel_text
            ) { _, _ ->
                dialog?.cancel()
            }
            builder.create()
        } ?: throw IllegalStateException("activity cannot be null")
    }

    private fun setUpElements() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.interval_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.intervalTypeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.interval_mes_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.intervalMesTypeSpinner.adapter = adapter
        }

        binding.intervalGoalLabel.text = resources.getString(R.string.seconds_text)
        binding.intervalMesTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (binding.intervalMesTypeSpinner.getItemAtPosition(p2) == resources.getStringArray(
                            R.array.interval_mes_types
                        )[INTERVAL_MEASUREMENT_DISTANCE]
                    ) {
                        binding.intervalGoalLabel.text = resources.getString(R.string.meters_text)
                    } else {
                        if (binding.intervalMesTypeSpinner.getItemAtPosition(p2) == resources.getStringArray(
                                R.array.interval_mes_types
                            )[INTERVAL_MEASUREMENT_TIME]
                        ) {
                            binding.intervalGoalLabel.text =
                                resources.getString(R.string.seconds_text)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    companion object {
        const val INTERVAL_REQUEST_KEY: String = "interval_req"
        const val INTERVAL_BUNDLE_KEY = "interval"
    }

}