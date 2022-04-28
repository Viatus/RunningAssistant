package com.example.runningassistant.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.runningassistant.BR
import com.example.runningassistant.R
import com.example.runningassistant.model.IntervalItem

class IntervalsRecyclerViewAdapter(private val intervalRecyclerAdapterInterface: IntervalRecyclerAdapterInterface) :
    RecyclerView.Adapter<IntervalsRecyclerViewAdapter.BindableViewHolder>() {

    interface IntervalRecyclerAdapterInterface {
        fun onIntervalLongClicked(position: Int):Boolean
    }

    var intervals: List<IntervalItem> = emptyList()

    override fun getItemCount(): Int {
        return intervals.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_interval,
            parent,
            false
        )
        return BindableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        holder.bind(intervals[position])
        holder.itemView.setOnLongClickListener {
            intervalRecyclerAdapterInterface.onIntervalLongClicked(position)
        }
    }

    fun updateItems(items: List<IntervalItem>?) {
        intervals = items ?: emptyList()
        notifyDataSetChanged()
    }


    class BindableViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(intervalItem: IntervalItem) {
            binding.setVariable(BR.intervalViewModel, intervalItem)
        }

    }
}