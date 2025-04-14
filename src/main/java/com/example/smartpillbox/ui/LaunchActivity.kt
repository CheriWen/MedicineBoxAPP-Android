package com.example.smartpillbox.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smartpillbox.R
import com.example.smartpillbox.utils.getLocalIpAddress

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvIp = findViewById<TextView>(R.id.tv_ip)
        val etEspIp = findViewById<EditText>(R.id.et_esp_ip)
        val btnConfirm = findViewById<Button>(R.id.btn_confirm)

        tvTitle.text = "请与你的智能药箱配对"
        tvIp.text = "当前局域网 IP: ${getLocalIpAddress()}\n请输入你的智能药箱 IP:"

        btnConfirm.setOnClickListener {
            val espIp = etEspIp.text.toString()
            if (espIp.isNotEmpty()) {
                // 保存用户输入的 IP 地址到 SharedPreferences
                val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("esp_ip", espIp)
                    apply()
                }
                // 跳转到主界面
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                etEspIp.error = "请输入智能药箱 IP"
            }
        }
    }
}