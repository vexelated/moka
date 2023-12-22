package com.capstoneapps.moka.ui.note.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstoneapps.moka.data.response.GetTaskResponse
import com.capstoneapps.moka.databinding.FragmentTaskBinding
import com.capstoneapps.moka.ui.ViewModelFactory
import com.capstoneapps.moka.ui.detail.DetailActivity
import com.capstoneapps.moka.ui.note.NoteViewModel
import com.capstoneapps.moka.ui.note.adapterTask.TaskAdapter
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Suppress("DEPRECATION")
class TaskFragment : Fragment(), TaskAdapter.TaskClickListener, TaskAdapter.TaskDoneClickListener {

    private lateinit var viewModel: NoteViewModel
    private lateinit var binding: FragmentTaskBinding
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)

        val viewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[NoteViewModel::class.java]

        binding.tambah.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_DETAIL)
        }

        val recyclerView: RecyclerView = binding.task
        taskAdapter = TaskAdapter(emptyList(), this, this) // Pass the fragment as TaskDoneClickListener
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tasksLiveData.observe(viewLifecycleOwner) { taskResponse ->
            taskResponse?.data?.let {
                taskAdapter.updateTaskList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTasksByStatus()
        }
    }

    override fun onDeleteIconClick(idTask: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteTask(idTask)
        }
    }

    override fun onDoneIconClick(idTask: String, taskName: String, taskDescription: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val requestBody = createUpdateRequestBody(idTask, taskName, taskDescription, true)
                val response = viewModel.updateTask(idTask, requestBody)

                if (response.status == 200) {
                    showToast("Task updated successfully")

                    // Remove the task with statusTask set to true from the view
                    val updatedTaskList = viewModel.tasksLiveData.value?.data?.toMutableList()
                    updatedTaskList?.removeAll { it?.idtask == idTask }
                    viewModel.tasksLiveData.value = GetTaskResponse(updatedTaskList)
                } else {
                    val errorMessage = response.message ?: "Failed to update task"
                    showToast(errorMessage)
                }
            } catch (e: Exception) {
                showToast("Error updating task: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == Activity.RESULT_OK) {
            // Handle the result from DetailActivity, if needed
            // val returnedData = data?.getStringExtra("key")

            // Panggil kembali data dari API setelah DetailActivity ditutup
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getTasksByStatus()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_DETAIL = 1001 // Angka apa pun sesuai dengan kebutuhan Anda

        // Function to create RequestBody for update task request
        private fun createUpdateRequestBody(idTask: String, taskName: String, taskDescription: String, statusTask: Boolean): RequestBody {
            val jsonBody = """
        {
            "idTask": "$idTask",
            "taskName": "$taskName",
            "taskDescription": "$taskDescription",
            "taskDate": null,
            "statusTask": $statusTask
        }
    """.trimIndent()

            return jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        }
    }
}
