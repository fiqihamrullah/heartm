package com.heartm.heartbeat.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import java.util.*

object AlarmScheduler
{
     private val NEXT_DRUG_TAKEN = 999
     private val PAGI_ALARM= 1001
     private val SIANG_ALARM= 1002
     private val SORE_ALARM= 1003


     fun scheduleNextDrugTaken(context: Context)
     {
         val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
                  type = "next_drug_taken"
         }


         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
         val pendingIntent = PendingIntent.getBroadcast(context,NEXT_DRUG_TAKEN,intent,0)

         //20 Detik
        // alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +1000*20,pendingIntent)
         //alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +1000*20,pendingIntent)

         val year  = 2021
         val month = 8
         val day_of_month = 25


         val calendar: Calendar = Calendar.getInstance().apply {
             timeInMillis = System.currentTimeMillis()
             set(Calendar.YEAR, year)
             set(Calendar.MONTH, month)
             set(Calendar.DAY_OF_MONTH, day_of_month)
             set(Calendar.HOUR_OF_DAY, 7)
             set(Calendar.MINUTE, 0)
         }

         alarmManager?.setRepeating(
             AlarmManager.RTC_WAKEUP,
             calendar.timeInMillis,
             AlarmManager.INTERVAL_HALF_DAY,
             pendingIntent
         )

     }


    fun scheduleDrinkInMorning(context: Context)
    {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            type = "drink_in_morning"
        }


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context,PAGI_ALARM,intent,0)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 30)
        }

       // alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +1000*60,pendingIntent)
      //  alarmManager?.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000*3,1000*6,pendingIntent)

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


    fun scheduleDrinkInAfternoon(context: Context)
    {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            type = "drink_in_afternoon"
        }


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, SIANG_ALARM,intent,0)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 13)
            set(Calendar.MINUTE, 30)
        }

        // alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +1000*60,pendingIntent)
        //  alarmManager?.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000*3,1000*6,pendingIntent)

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


    fun scheduleDrinkInEvening(context: Context)
    {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            type = "drink_in_evening"
        }


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, SORE_ALARM,intent,0)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 30)
        }

        // alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +1000*60,pendingIntent)
        //  alarmManager?.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000*3,1000*6,pendingIntent)

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


    fun cancelSchedule(context: Context)
    {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val pendingIntent =
            PendingIntent.getService(context, PAGI_ALARM, intent,
                PendingIntent.FLAG_NO_CREATE)


        val pendingIntent2 =
            PendingIntent.getService(context, SIANG_ALARM, intent,
                PendingIntent.FLAG_NO_CREATE)


        val pendingIntent3 =
            PendingIntent.getService(context, SORE_ALARM, intent,
                PendingIntent.FLAG_NO_CREATE)

        if (pendingIntent != null && alarmManager != null)
        {
            alarmManager.cancel(pendingIntent)
            alarmManager.cancel(pendingIntent2)
            alarmManager.cancel(pendingIntent3)
        }
    }

}