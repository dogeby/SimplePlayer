package com.yang.simpleplayer.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object Format {
    fun msToHourMinSecond(ms:Int): String {
        val seconds = ms / 1000 % 60
        val mins = ms / 60000 % 60
        val hours = ms / 3600000 % 60
        return if(hours == 0) String.format("%02d:%02d", mins, seconds)
        else String.format("%02d:%02d:%02d", hours, mins, seconds)
    }

    fun getParentFolderName(relativePath:String):String {
        val tokens = relativePath.split('/')
        return tokens[tokens.lastIndex-1]
    }

    fun splitExtension(fileName:String) = fileName.substring(0, fileName.lastIndexOf('.'))

    @SuppressLint("SimpleDateFormat")
    fun dateToKorDate(date: Date):String {
        val dateFormatIncludeYear = SimpleDateFormat("yyyy년 MM월 dd일")
        val dateFormat = SimpleDateFormat("MM월 dd일")
        val paramCalender = GregorianCalendar()
        paramCalender.time = date
        return if(paramCalender.get(Calendar.YEAR) == GregorianCalendar().get(Calendar.YEAR)) dateFormat.format(date)
        else dateFormatIncludeYear.format(date)
    }
}