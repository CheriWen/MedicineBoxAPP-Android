package com.example.smartpillbox.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smartpillbox.R
import com.example.smartpillbox.network.HttpServer
import com.example.smartpillbox.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var httpServer: HttpServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 从 SharedPreferences 获取用户输入的 IP 地址
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val espIp = sharedPref.getString("esp_ip", "192.168.1.101") ?: "192.168.1.101"
        val baseUrl = "http://$espIp/"

        // 初始化 RetrofitClient
        RetrofitClient.init(baseUrl)

        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        httpServer = HttpServer(this)
        httpServer.startServer()
    }
}