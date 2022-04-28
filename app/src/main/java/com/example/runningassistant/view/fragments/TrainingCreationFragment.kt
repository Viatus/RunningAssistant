package com.example.runningassistant.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentTrainingCreationBinding
import com.example.runningassistant.model.TrainingTableModel
import com.example.runningassistant.view.adapters.IntervalsRecyclerViewAdapter
import com.example.runningassistant.view.fragments.NewIntervalDialogFragment.Companion.INTERVAL_BUNDLE_KEY
import com.example.runningassistant.view.fragments.NewIntervalDialogFragment.Companion.INTERVAL_REQUEST_KEY
import com.example.runningassistant.model.IntervalItem
import com.example.runningassistant.viewmodel.TrainingViewModel

class TrainingCreationFragment : Fragment(),
    IntervalsRecyclerViewAdapter.IntervalRecyclerAdapterInterface {

    private lateinit var binding: FragmentTrainingCreationBinding
    private val trainingViewModel: TrainingViewModel by activityViewModels()

    private var isCreatingNew = false
    private var trainingDetails: TrainingTableModel? = null

    private val args: TrainingCreationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_training_creation, container, false)

        binding.intervalRecyclerAdapterInterfaceImpl = this

        setUpLiveDataObservers()
        setUpListeners()

        val decoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        decoration.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.recyclerview_divider
            )!!
        )
        binding.intervalsRecyclerView.addItemDecoration(
            decoration
        )

        return binding.root
    }

    private fun setUpListeners() {
        binding.buttonCreateTraining.setOnClickListener {
            if (isCreatingNew) {
                trainingViewModel.insertTraining(
                    requireContext(),
                    binding.trainingName.text.toString(),
                    binding.intervalsList ?: emptyList(),
                    if (binding.repeatsEditText.text.toString() == "") 0 else binding.repeatsEditText.text.toString()
                        .toInt(),
                    binding.warmUpCheck.isChecked,
                    binding.coolDownCheck.isChecked
                )
            } else {
                trainingDetails!!.Title = binding.trainingName.text.toString()
                trainingDetails!!.Cooldown = binding.coolDownCheck.isChecked
                trainingDetails!!.WarmUp = binding.warmUpCheck.isChecked
                trainingDetails!!.Repeats = 0
                trainingDetails!!.TrainingPlan = binding.intervalsList ?: emptyList()

                trainingDetails?.let { it2 ->
                    trainingViewModel.updateTrainingModel(
                        requireContext(),
                        it2
                    )
                }
            }

            trainingViewModel.getAllTrainingNames(requireContext())
            findNavController().navigate(R.id.action_trainingCreationFragment_to_trainingSelectionFragment)
        }

        binding.addInterval.setOnClickListener {
            val dialogFragment = NewIntervalDialogFragment()
            dialogFragment.show(childFragmentManager, "NewIntervalDialogFragment")
            dialogFragment.setFragmentResultListener(INTERVAL_REQUEST_KEY) { key, bundle ->
                if (key == INTERVAL_REQUEST_KEY) {
                    val list = binding.intervalsList
                    val intervalArray = bundle.getIntArray(INTERVAL_BUNDLE_KEY)
                    list?.add(
                        IntervalItem(
                            intervalArray?.get(0) ?: 0,
                            intervalArray?.get(1) ?: 0, (intervalArray?.get(2) ?: 0).toFloat()
                        )
                    )
                    binding.intervalsList = list
                }
            }
        }
    }

    private fun setUpLiveDataObservers() {
        if (args.trainingTitle != null) {
            binding.trainingName.isEnabled = false
            binding.intervalsRecyclerView.isEnabled = false
            binding.warmUpCheck.isEnabled = false
            binding.coolDownCheck.isEnabled = false
            binding.repeatsEditText.isEnabled = false
            trainingViewModel.getTrainingDetails(requireContext(), args.trainingTitle!!)
                ?.observe(viewLifecycleOwner, object : Observer<TrainingTableModel> {
                    override fun onChanged(t: TrainingTableModel?) {
                        trainingDetails = trainingViewModel.liveDataTrainingModel?.value

                        if (trainingDetails == null) {
                            trainingDetails =
                                TrainingTableModel(
                                    "",
                                    emptyList(),
                                    0,
                                    WarmUp = false,
                                    Cooldown = false
                                )
                            isCreatingNew = true
                        }
                        binding.intervalsList = trainingDetails?.TrainingPlan
                        binding.trainingName.setText(trainingDetails?.Title)
                        binding.warmUpCheck.isChecked = trainingDetails?.WarmUp == true
                        binding.coolDownCheck.isChecked = trainingDetails?.Cooldown == true

                        binding.trainingName.isEnabled = true
                        binding.intervalsRecyclerView.isEnabled = true
                        binding.warmUpCheck.isEnabled = true
                        binding.coolDownCheck.isEnabled = true
                        binding.repeatsEditText.isEnabled = true

                        trainingViewModel.liveDataTrainingModel?.removeObserver(this)
                    }
                })
        } else {
            isCreatingNew = true

            trainingDetails =
                TrainingTableModel(
                    "",
                    arrayListOf(),
                    0,
                    WarmUp = false,
                    Cooldown = false
                )

            binding.intervalsList = trainingDetails?.TrainingPlan
            binding.trainingName.setText(trainingDetails?.Title)
            binding.warmUpCheck.isChecked = trainingDetails?.WarmUp == true
            binding.coolDownCheck.isChecked = trainingDetails?.Cooldown == true
        }
    }

    override fun onIntervalLongClicked(position: Int): Boolean {
        val list = binding.intervalsList
        list?.removeAt(position)
        binding.intervalsList = list
        return true
    }
}