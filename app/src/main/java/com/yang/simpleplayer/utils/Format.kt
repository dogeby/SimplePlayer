package com.yang.simpleplayer.utils

import java.util.concurrent.TimeUnit

object Format {
    fun msToHourMinSecond(ms:Long): String {
        val second = ms / 1000 % 60
        val min = ms / (1000*60) %60
        val hour = ms / (1000*60*60) % 60
        val duration = StringBuilder()
        if(hour>0) {
            if(hour<10) duration.append(0)
            duration.append(hour)
            duration.append(':')
        }
        if(min>0) {
            if(min<10) duration.append(0)
            duration.append(min)
        }
        else duration.append("00")
        duration.append(':')
        if(second>0) {
            if(second<10) duration.append(0)
            duration.append(second)
        }
        else duration.append("00")
        return duration.toString()
    }
}