package com.example.runningassistant.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.runningassistant.BR
import com.example.runningassistant.R

class TrainingsRecyclerViewAdapter(private val onTrainingClickHandler: OnTrainingClickHandlerInterface):
    RecyclerView.Adapter<TrainingsRecyclerViewAdapter.BindableViewHolder>() {

    private var trainingNames: List<String> = emptyList()

    override fun getItemCount(): Int {
        return trainingNames.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_training,
            parent,
            false
        )
        return BindableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        holder.bind(trainingNames[position], onTrainingClickHandler)

    }

    fun updateItems(items: List<String>?) {
        Log.i("Adapter update", items.toString())
        trainingNames = items ?: emptyList()
        notifyDataSetChanged()
    }


    class BindableViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trainingName: String, onTrainingClickHandler: OnTrainingClickHandlerInterface) {
            binding.setVariable(BR.trainingName, trainingName)
            binding.root.setOnLongClickListener{onTrainingClickHandler.onTrainingLongClick(trainingName)}
            binding.root.setOnClickListener { onTrainingClickHandler.onTrainingClick(trainingName) }
        }

    }
}

interface OnTrainingClickHandlerInterface {
    fun onTrainingLongClick(trainingName: String): Boolean
    fun onTrainingClick(trainingName: String)
}