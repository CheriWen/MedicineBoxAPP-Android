package com.example.smartpillbox.utils

import java.net.NetworkInterface

fun getLocalIpAddress(): String {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val intf = interfaces.nextElement()
            val addrs = intf.inetAddresses
            while (addrs.hasMoreElements()) {
                val addr = addrs.nextElement()
                if (!addr.isLoopbackAddress && addr.isSiteLocalAddress) {
                    return addr.hostAddress ?: "无法获取 IP 地址"
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return "无法获取 IP 地址"
}