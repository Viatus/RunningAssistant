package com.example.runningassistant.view.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.runningassistant.model.IntervalItem
import com.example.runningassistant.model.TrainingResultTableModel

@BindingAdapter(value = ["intervalItemViewModels", "onIntervalLongClickedHandler"])
fun bindIntervalItemViewModels(
    recyclerView: RecyclerView,
    intervalItems: List<IntervalItem>?,
    intervalRecyclerAdapterInterface: IntervalsRecyclerViewAdapter.IntervalRecyclerAdapterInterface
) {
    val adapter = getOrCreateIntervalAdapter(recyclerView, intervalRecyclerAdapterInterface)
    adapter.updateItems(intervalItems)
}

private fun getOrCreateIntervalAdapter(
    recyclerView: RecyclerView,
    intervalRecyclerAdapterInterface: IntervalsRecyclerViewAdapter.IntervalRecyclerAdapterInterface
): IntervalsRecyclerViewAdapter {
    return if (recyclerView.adapter != null && recyclerView.adapter is IntervalsRecyclerViewAdapter) {
        recyclerView.adapter as IntervalsRecyclerViewAdapter
    } else {
        val intervalsRecyclerViewAdapter = IntervalsRecyclerViewAdapter(intervalRecyclerAdapterInterface)
        recyclerView.adapter = intervalsRecyclerViewAdapter
        intervalsRecyclerViewAdapter
    }
}

@BindingAdapter(value = ["trainingNames", "onTrainingClickHandler"])
fun bindTrainingNames(
    recyclerView: RecyclerView,
    trainingNames: List<String>?,
    onTrainingClickHandler: OnTrainingClickHandlerInterface
) {
    val adapter = getOrCreateTrainingAdapter(recyclerView, onTrainingClickHandler)
    adapter.updateItems(trainingNames)
}

private fun getOrCreateTrainingAdapter(
    recyclerView: RecyclerView,
    onTrainingClickHandler: OnTrainingClickHandlerInterface
): TrainingsRecyclerViewAdapter {
    return if (recyclerView.adapter != null && recyclerView.adapter is TrainingsRecyclerViewAdapter) {
        recyclerView.adapter as TrainingsRecyclerViewAdapter
    } else {
        val trainingsRecyclerViewAdapter = TrainingsRecyclerViewAdapter(onTrainingClickHandler)
        recyclerView.adapter = trainingsRecyclerViewAdapter
        trainingsRecyclerViewAdapter
    }
}

@BindingAdapter(value = ["trainingResults", "onTrainingResultClickHandler"])
fun bindTrainingResults(
    recyclerView: RecyclerView,
    trainingResults: List<TrainingResultTableModel>?,
    onTrainingResultClickHandler: TrainingResultsRecyclerViewAdapter.OnTrainingResultClickHandlerInterface
) {
    val adapter = getOrCreateTrainingResultAdapter(recyclerView, onTrainingResultClickHandler)
    adapter.updateItems(trainingResults)
}

private fun getOrCreateTrainingResultAdapter(
    recyclerView: RecyclerView,
    onTrainingResultClickHandler: TrainingResultsRecyclerViewAdapter.OnTrainingResultClickHandlerInterface
): TrainingResultsRecyclerViewAdapter {
    return if (recyclerView.adapter != null && recyclerView.adapter is TrainingResultsRecyclerViewAdapter) {
        recyclerView.adapter as TrainingResultsRecyclerViewAdapter
    } else {
        val trainingResultsRecyclerViewAdapter = TrainingResultsRecyclerViewAdapter(onTrainingResultClickHandler)
        recyclerView.adapter = trainingResultsRecyclerViewAdapter
        trainingResultsRecyclerViewAdapter
    }
}