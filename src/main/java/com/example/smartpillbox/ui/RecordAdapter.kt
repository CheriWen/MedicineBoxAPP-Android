package com.example.smartpillbox.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartpillbox.R
import com.example.smartpillbox.data.MedRecord

class RecordAdapter(private val records: MutableList<MedRecord>) :
    RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSlot: TextView = itemView.findViewById(R.id.tv_slot)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.tvSlot.text = "药格 ${record.medicineId}"
        holder.tvTime.text = record.time
        holder.tvStatus.text = record.status // 确保 MedRecord 有 status 字段
    }

    override fun getItemCount() = records.size

    fun updateData(newRecords: List<MedRecord>) {
        records.clear()
        records.addAll(newRecords)
        notifyDataSetChanged()
    }
}