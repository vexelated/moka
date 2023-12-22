package com.capstoneapps.moka.ui.note.adapterTask

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstoneapps.moka.data.response.DataItem
import com.capstoneapps.moka.databinding.CardItemHistoryBinding

class HistoryAdapter(
    private var taskList: List<DataItem?>,

) : RecyclerView.Adapter<HistoryAdapter.TaskViewHolder>() {
    init {
        // Enable stable IDs for preventing view recycling
        setHasStableIds(true)
    }

    // Override getItemId to return a unique identifier for each item
    override fun getItemId(position: Int): Long {
        val taskId = taskList[position]?.idtask
        return try {
            taskId?.toLong() ?: RecyclerView.NO_ID
        } catch (e: NumberFormatException) {
            // Handle the case where taskId is not a valid Long
            RecyclerView.NO_ID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = CardItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]
        currentItem?.let {
            holder.binding.titleTextView.text = it.taskname
            holder.binding.descriptionTextView.text = it.taskdescription

        }
    }
    override fun getItemCount() = taskList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTaskList(newTaskList: List<DataItem?>) {
        taskList = newTaskList.filter { it?.statustask == 1 }
        notifyDataSetChanged()
    }

    class TaskViewHolder(val binding: CardItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
