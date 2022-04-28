package com.example.runningassistant.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningassistant.R
import com.example.runningassistant.databinding.FragmentTrainingSelectionBinding
import com.example.runningassistant.view.adapters.OnTrainingClickHandlerInterface
import com.example.runningassistant.viewmodel.TrainingViewModel

class TrainingSelectionFragment : Fragment(), OnTrainingClickHandlerInterface {

    private lateinit var binding: FragmentTrainingSelectionBinding
    private val trainingViewModel: TrainingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_training_selection,
            container,
            false
        )

        binding.onTrainingClickHandler = this

        binding.trainingsList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.HORIZONTAL
            )
        )

        val decoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        decoration.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.recyclerview_divider
            )!!
        )
        binding.trainingsList.addItemDecoration(
            decoration
        )

        setUpLiveDataObservers()

        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
        binding.buttonAddTraining.setOnClickListener {
            val action =
                TrainingSelectionFragmentDirections.actionTrainingSelectionFragmentToTrainingCreationFragment(
                    null
                )
            findNavController().navigate(action)
        }
    }

    private fun setUpLiveDataObservers() {
        trainingViewModel.getAllTrainingNames(requireContext())
            ?.observe(viewLifecycleOwner, Observer { list ->
                binding.trainingNames =
                    list ?: emptyList()
                Log.i(
                    "Selection names",
                    list.toString()
                )
            })
        trainingViewModel.getAllTrainingNames(requireContext())
    }

    override fun onTrainingLongClick(trainingName: String): Boolean {
        val action =
            TrainingSelectionFragmentDirections.actionTrainingSelectionFragmentToTrainingCreationFragment(
                trainingName
            )
        findNavController().navigate(action)
        return true
    }

    override fun onTrainingClick(trainingName: String) {
        trainingViewModel.updateChosenTrainingName(trainingName)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        sharedPref?.edit()
            ?.putString(getString(R.string.preference_training_name_key), trainingName)?.apply()
        findNavController().navigate(R.id.action_trainingSelectionFragment_to_mainFragment)
    }
}