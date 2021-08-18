package com.heartm.heartbeat.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

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

     }


    fun scheduleDrinkInMorning(context: Context)
    {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            type = "drink_in_morning"
        }


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context,PAGI_ALARM,intent,0)

       // alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +1000*60,pendingIntent)
      //  alarmManager?.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000*3,1000*6,pendingIntent)
    }

    fun cancelSchedule(context: Context)
    {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent =
            PendingIntent.getService(context, PAGI_ALARM, intent,
                PendingIntent.FLAG_NO_CREATE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

}