package com.heartm.heartbeat.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyDateConverter
{
    fun convertfromShortDate(inputDate: String?,pattern:String?): String?
    {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            date = simpleDateFormat.parse(inputDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date == null) {
            return ""
        }
        val convetDateFormat = SimpleDateFormat(pattern)
        return convetDateFormat.format(date)
    }


    fun convertfromLongDate(inputDate: String?,pattern:String?): String?
    {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        var date: Date? = null
        try {
            date = simpleDateFormat.parse(inputDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date == null) {
            return ""
        }
        val convetDateFormat = SimpleDateFormat(pattern)
        return convetDateFormat.format(date)
    }

}