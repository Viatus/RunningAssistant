package com.example.runningassistant.view.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentTrainingResultsBinding
import com.example.runningassistant.view.MainActivity
import com.example.runningassistant.view.TrainingActivity
import com.example.runningassistant.view.adapters.TrainingResultsRecyclerViewAdapter
import com.example.runningassistant.viewmodel.TrainingViewModel

class TrainingResultsFragment : Fragment(),
    TrainingResultsRecyclerViewAdapter.OnTrainingResultClickHandlerInterface {
    private lateinit var binding: FragmentTrainingResultsBinding

    private val trainingViewModel: TrainingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training_results, null, false)
        binding.onTrainingResultClickHandler = this
        binding.trainingResultsList = emptyList()
        setUpListeners()
        setupLiveDataObservers()
        return binding.root
    }

    private fun setupLiveDataObservers() {
        trainingViewModel.getAllTrainingResults(requireContext())
            ?.observe(viewLifecycleOwner) { trainingResults ->
                binding.trainingResultsList = trainingResults
            }
    }

    private fun setUpListeners() {
        binding.buttonGoToTrainingScreen.setOnClickListener {
            if ((activity as MainActivity).checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    1
                ) && (activity as MainActivity).checkPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    2
                )
            ) {
                val intent = Intent(requireContext(), TrainingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onTrainingResultsClicked() {
        //nothing for now
    }

}