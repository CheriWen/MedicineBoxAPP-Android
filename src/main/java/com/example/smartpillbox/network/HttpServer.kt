package com.example.smartpillbox.network

import android.content.Context
import com.example.smartpillbox.data.DatabaseProvider
import com.example.smartpillbox.data.MedRecord
import com.example.smartpillbox.ui.ReminderReceiver
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HttpServer(private val context: Context) : NanoHTTPD(8080) {

    override fun serve(session: IHTTPSession): Response {
        if (session.uri == "/notify" && session.method == Method.GET) {
            val params = session.parms
            val slot = params["slot"]?.toIntOrNull() ?: return newFixedLengthResponse("Invalid slot")
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val record = MedRecord(medicineId = slot, time = time, status = "已取药", period = 1, dosage = 2)
            CoroutineScope(Dispatchers.IO).launch {
                DatabaseProvider.getDatabase(context).medRecordDao().insert(record)
                ReminderReceiver.stopRingtone()
            }

            return newFixedLengthResponse("OK")
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
    }

    fun startServer() {
        try {
            start(SOCKET_READ_TIMEOUT, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}