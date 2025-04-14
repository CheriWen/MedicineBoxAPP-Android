package com.example.smartpillbox.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartpillbox.R
import com.example.smartpillbox.data.DatabaseProvider
import com.example.smartpillbox.data.Medicine
import com.example.smartpillbox.databinding.FragmentMedicineBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MedicineFragment : Fragment() {

    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()
    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhoto: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MedicineAdapter(medicines) { medicine ->
            lifecycleScope.launch {
                DatabaseProvider.getDatabase(requireContext()).medicineDao().delete(medicine)
                loadMedicines()
            }
        }
        binding.rvMedicines.layoutManager = LinearLayoutManager(context)
        binding.rvMedicines.adapter = adapter

        binding.btnAdd.setOnClickListener { showAddDialog() }
        loadMedicines()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadMedicines() {
        lifecycleScope.launch {
            val list = DatabaseProvider.getDatabase(requireContext()).medicineDao().getAll()
            adapter.updateData(list)
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_medicine, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("新增药品")
            .setView(dialogView)
            .setPositiveButton("添加") { _, _ ->
                val name = dialogView.findViewById<android.widget.EditText>(R.id.et_name).text.toString()
                val expiry = dialogView.findViewById<android.widget.EditText>(R.id.et_expiry).text.toString()
                val type = dialogView.findViewById<android.widget.EditText>(R.id.et_type).text.toString()
                val slot = dialogView.findViewById<android.widget.EditText>(R.id.et_slot).text.toString().toIntOrNull() ?: 0
                lifecycleScope.launch {
                    val medicine = Medicine(name = name, expiryDate = expiry, type = type, slot = slot, photo = currentPhoto)
                    DatabaseProvider.getDatabase(requireContext()).medicineDao().insert(medicine)
                    loadMedicines()
                    currentPhoto = null
                }
            }
            .setNegativeButton("取消", null)
            .create()

        dialogView.findViewById<View>(R.id.btn_take_photo).setOnClickListener {
            dispatchTakePictureIntent()
        }
        dialog.show()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            currentPhoto = data?.extras?.get("data") as Bitmap
        }
    }
}