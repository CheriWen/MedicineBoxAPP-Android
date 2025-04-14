package com.example.smartpillbox.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.appcompat.app.AlertDialog
import com.example.smartpillbox.data.DatabaseProvider
import com.example.smartpillbox.data.MedRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.smartpillbox.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        private var ringtone: Ringtone? = null
        private var dialog: AlertDialog? = null

        @JvmStatic
        fun stopRingtone() {
            ringtone?.stop()
            dialog?.dismiss()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val slot = intent.getIntExtra("slot", -1)
        if (slot != -1) {
            showReminderDialog(context, slot)
        }
    }

    private fun showReminderDialog(context: Context, slot: Int) {
        // 播放通知声音
        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(context, notificationUri)
        ringtone?.play()

        // 第一次确认弹窗
        val builder = AlertDialog.Builder(context)
        builder.setTitle("吃药提醒")
        builder.setMessage("药格 $slot：该吃药了！")
        builder.setPositiveButton("确认") { _, _ ->
            // 第二次确认弹窗
            AlertDialog.Builder(context)
                .setTitle("确认操作")
                .setMessage("确定已取药或取消提醒？")
                .setPositiveButton("已取药") { _, _ ->
                    stopRingtone()
                    updateRecord(context, slot, "已取药")
                }
                .setNegativeButton("取消提醒") { _, _ ->
                    stopRingtone()
                    updateRecord(context, slot, "用户手动取消")
                }
                .setCancelable(false)
                .show()
        }
        builder.setNegativeButton("稍后") { dialog, _ -> dialog.dismiss() }
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.show()
    }

    private fun updateRecord(context: Context, slot: Int, status: String) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val record = MedRecord(medicineId = slot, time = time, status = status, period = 1, dosage = 2)
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseProvider.getDatabase(context).medRecordDao().insert(record)
        }

        if (status == "已取药") {
            RetrofitClient.apiService?.finishMed()?.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        android.util.Log.d("FinishMed", "Finish med request successful")
                    } else {
                        android.util.Log.e("FinishMed", "Failed with code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    android.util.Log.e("FinishMed", "Request failed: ${t.message}")
                }
            })
        }
    }
}