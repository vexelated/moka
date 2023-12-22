package com.capstoneapps.moka.ui.note.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstoneapps.moka.databinding.FragmentHistoryBinding
import com.capstoneapps.moka.ui.ViewModelFactory
import com.capstoneapps.moka.ui.note.NoteViewModel
import com.capstoneapps.moka.ui.note.adapterTask.HistoryAdapter
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class HistoryFragment : Fragment(){

    private lateinit var viewModel: NoteViewModel
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val viewModelFactory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[NoteViewModel::class.java]

        val recyclerView: RecyclerView = binding.history
        historyAdapter = HistoryAdapter(emptyList()) // Pass the fragment as TaskDoneClickListener
        recyclerView.adapter = historyAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tasksLiveData.observe(viewLifecycleOwner) { taskResponse ->
            taskResponse?.data?.let {
                historyAdapter.updateTaskList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTasksByStatusHistory()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == Activity.RESULT_OK) {
            // Handle the result from DetailActivity, if needed
            // val returnedData = data?.getStringExtra("key")

            // Panggil kembali data dari API setelah DetailActivity ditutup
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getTasksByStatusHistory()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_DETAIL = 1001 // Angka apa pun sesuai dengan kebutuhan Anda
    }
}
