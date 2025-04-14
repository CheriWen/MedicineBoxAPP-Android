package com.example.smartpillbox.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartpillbox.databinding.FragmentRecordBinding
import com.example.smartpillbox.data.DatabaseProvider
import com.example.smartpillbox.data.MedRecord
import kotlinx.coroutines.launch

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecordAdapter
    private val records = mutableListOf<MedRecord>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecordAdapter(records)
        binding.rvRecords.layoutManager = LinearLayoutManager(context)
        binding.rvRecords.adapter = adapter

        loadRecords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadRecords() {
        lifecycleScope.launch {
            val list = DatabaseProvider.getDatabase(requireContext()).medRecordDao().getAll()
            adapter.updateData(list)
        }
    }
}