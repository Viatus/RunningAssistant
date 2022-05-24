package com.example.runningassistant.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.runningassistant.R
import com.example.runningassistant.databinding.ItemTrainingResultBinding
import com.example.runningassistant.model.TrainingResultTableModel
import java.lang.StringBuilder
import java.util.*

class TrainingResultsRecyclerViewAdapter(private val onTrainingResultClickHandler: OnTrainingResultClickHandlerInterface) :
    RecyclerView.Adapter<TrainingResultsRecyclerViewAdapter.BindableViewHolder>() {

    interface OnTrainingResultClickHandlerInterface {
        fun onTrainingResultsClicked(trainingResult: TrainingResultTableModel)
    }

    private var trainingResults: List<TrainingResultTableModel> = emptyList()

    override fun getItemCount(): Int {
        return trainingResults.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_training_result,
            parent,
            false
        )
        return BindableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        holder.bind(trainingResults[position], onTrainingResultClickHandler)

    }

    fun updateItems(items: List<TrainingResultTableModel>?) {
        Log.i("Adapter update", items.toString())
        trainingResults = items ?: emptyList()
        notifyDataSetChanged()
    }


    class BindableViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            trainingResult: TrainingResultTableModel,
            onTrainingResultClickHandler: OnTrainingResultClickHandlerInterface
        ) {
            binding as ItemTrainingResultBinding
            when (trainingResult.Condition) {
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

            val calendar = Calendar.getInstance()
            calendar.time = trainingResult.TrainingDate

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
                in 18..21 -> dateStr.append(" - Evening")
            }

            binding.dateTextView.text = dateStr.toString()

            binding.totalDistanceTexView.text =
                String.format(Locale.getDefault(), "%.2f", trainingResult.TotalDistance / 1000)
            binding.totalTimeTexView.text =
                String.format(
                    Locale.getDefault(),
                    "%d:%02d",
                    trainingResult.TotalTime / 60,
                    trainingResult.TotalTime % 60
                )
            binding.averageSpeedTextView.text =
                String.format(Locale.getDefault(), "%.2f", trainingResult.TotalDistance / 1000 / trainingResult.TotalTime * 3600)

            binding.root.setOnClickListener {
                onTrainingResultClickHandler.onTrainingResultsClicked(trainingResult)
            }
        }

    }
}