package com.heartm.heartbeat.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (context!=null)
        {

            if (intent?.type.equals("next_drug_taken",ignoreCase = true))
            {
                NotificationHelper.createSimpleNotification(
                    context,
                    "Pengambilan Obat",
                    "Jangan Lupa untuk mengambil Obat ",
                    "",
                    true,
                    1001
                )
            }else if (intent?.type.equals("drink_in_morning",ignoreCase = true))
            {
                NotificationHelper.createSimpleNotification(
                    context,
                    "Ingat,Minum Obat ya[PAGI]",
                    "Waktunya Minum Obat, semoga lekas sembuh!",
                    "",
                    true,
                    1002
                )

            }else if (intent?.type.equals("drink_in_afternoon",ignoreCase = true))
            {
                NotificationHelper.createSimpleNotification(
                    context,
                    "Ingat,Minum Obat ya[SIANG]",
                    "Waktunya minum Obat, semoga lekas sembuh!",
                    "",
                    true,
                    1002
                )

            }else if (intent?.type.equals("drink_in_evening",ignoreCase = true))
            {
                NotificationHelper.createSimpleNotification(
                    context,
                    "Ingat,Minum Obat ya[SORE]",
                    "Waktunya minum obat, semoga lekas sembuh!",
                    "",
                    true,
                    1002
                )

            }


            if (intent?.action == "android.intent.action.BOOT_COMPLETED")
            {
                // Set the alarm here.

            }


        }
    }

}