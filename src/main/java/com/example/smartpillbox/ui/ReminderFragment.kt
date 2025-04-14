package com.example.smartpillbox.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.smartpillbox.R
import com.example.smartpillbox.data.DatabaseProvider
import com.example.smartpillbox.data.MedRecord
import com.example.smartpillbox.databinding.FragmentReminderBinding
import com.example.smartpillbox.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSetReminder.setOnClickListener {
            val slot = binding.etSlot.text.toString().toIntOrNull() ?: return@setOnClickListener
            val period = binding.etPeriod.text.toString().toIntOrNull() ?: return@setOnClickListener
            val dosage = binding.etDosage.text.toString().toIntOrNull() ?: return@setOnClickListener
            val hour = binding.timePicker.hour
            val minute = binding.timePicker.minute

            // 参数范围检查（根据 ESP32 要求调整）
            if (slot !in 1..16) {  // 假设 ESP32 支持 1-16，需与 ESP32 代码确认
                Toast.makeText(context, "药格号必须在 1-16 之间", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (period !in 0..3) {  // 假设 period 范围为 0-3
                Toast.makeText(context, "时段必须在 0-3 之间", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (hour !in 0..23 || minute !in 0..59) {
                Toast.makeText(context, "时间格式错误", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dosage < 1) {
                Toast.makeText(context, "剂量必须大于等于 1", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 发送提醒时间给 ESP32
            RetrofitClient.apiService?.setReminder(slot, period, hour, minute, dosage)?.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    android.util.Log.d("SetReminder", "Response code: ${response.code()}")
                    if (response.isSuccessful) {
                        Toast.makeText(context, "提醒时间已发送至 ESP32", Toast.LENGTH_SHORT).show()
                        setLocalAlarm(hour, minute, slot)
                        saveReminderRecord(slot, hour, minute, period, dosage)
                    } else {
                        Toast.makeText(context, "发送失败: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    android.util.Log.e("SetReminder", "Request failed: ${t.message}")
                    Toast.makeText(context, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLocalAlarm(hour: Int, minute: Int, slot: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java).apply {
            putExtra("slot", slot)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), slot, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "本地提醒已设定", Toast.LENGTH_SHORT).show()
    }

    private fun saveReminderRecord(slot: Int, hour: Int, minute: Int, period: Int, dosage: Int) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }.time
        )
        val record = MedRecord(medicineId = slot, time = time, status = "未取药", period = period, dosage = dosage)
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseProvider.getDatabase(requireContext()).medRecordDao().insert(record)
        }
    }
}