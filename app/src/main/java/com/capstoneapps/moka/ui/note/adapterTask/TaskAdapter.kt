package com.capstoneapps.moka.ui.note.adapterTask

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstoneapps.moka.data.response.DataItem
import com.capstoneapps.moka.databinding.CardItemNoteBinding

class TaskAdapter(
    private var taskList: List<DataItem?>,
    private val clickListener: TaskClickListener,
    private val doneClickListener: TaskDoneClickListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    interface TaskClickListener {
        fun onDeleteIconClick(idTask: String)
    }

    interface TaskDoneClickListener {
        fun onDoneIconClick(idTask: String,taskName: String,taskDescription: String)
    }

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
        val binding =
            CardItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]

        currentItem?.let {
            holder.binding.titleTextView.text = it.taskname
            holder.binding.descriptionTextView.text = it.taskdescription

            holder.binding.done.setOnClickListener {
                val taskId = taskList[position]?.idtask
                val taskName = taskList[position]?.taskname
                val taskDescription = taskList[position]?.taskdescription
                taskId?.let { idTask ->
                    if (taskName != null) {
                        if (taskDescription != null) {
                            doneClickListener.onDoneIconClick(idTask,taskName,taskDescription)
                        }
                    }
                }
            }

            holder.binding.deleteIcon.setOnClickListener {
                val taskId = taskList[position]?.idtask

                taskId?.let { idTask ->
                    clickListener.onDeleteIconClick(idTask)
                }
            }
        }
    }

    override fun getItemCount() = taskList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTaskList(newTaskList: List<DataItem?>) {
        taskList = newTaskList.filter { it?.statustask == 0 }
        notifyDataSetChanged()
    }

    class TaskViewHolder(val binding: CardItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root)
}
