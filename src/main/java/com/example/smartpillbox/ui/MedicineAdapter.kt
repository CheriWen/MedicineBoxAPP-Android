package com.example.smartpillbox.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartpillbox.R
import com.example.smartpillbox.data.Medicine

class MedicineAdapter(
    private val medicines: MutableList<Medicine>,
    private val onDelete: (Medicine) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvExpiry: TextView = itemView.findViewById(R.id.tv_expiry)
        val tvType: TextView = itemView.findViewById(R.id.tv_type)
        val tvSlot: TextView = itemView.findViewById(R.id.tv_slot)
        val btnDelete: View = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.tvName.text = medicine.name
        holder.tvExpiry.text = "有效期: ${medicine.expiryDate}"
        holder.tvType.text = "类型: ${medicine.type}"
        holder.tvSlot.text = "格号: ${medicine.slot}"
        holder.btnDelete.setOnClickListener { onDelete(medicine) }
    }

    override fun getItemCount() = medicines.size

    fun updateData(newMedicines: List<Medicine>) {
        medicines.clear()
        medicines.addAll(newMedicines)
        notifyDataSetChanged()
    }
}