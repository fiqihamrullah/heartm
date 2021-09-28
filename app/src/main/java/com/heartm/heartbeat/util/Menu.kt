package com.heartm.heartbeat.util

import android.content.Context
import android.content.Intent
import android.view.ViewGroup

import android.widget.ImageView

import androidx.core.content.ContextCompat
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.heartm.heartbeat.*

import com.ismaeldivita.chipnavigation.ChipNavigationBar


class Menu(context: Context)
{


    private var lastColor: Int = 0
    private var context : Context

    init
    {
       this.context = context
    }


    fun initMenu(container : ViewGroup, button: ImageView, menu:ChipNavigationBar)
    {
        lastColor = ContextCompat.getColor(context, R.color.blank)

        menu.setOnItemSelectedListener(object : ChipNavigationBar.OnItemSelectedListener
        {
            override fun onItemSelected(id: Int)
            {
                /*
                val option = when (id) {
                    R.id.home -> R.color.home to "Dashboard"
                    R.id.activity -> R.color.activity to "Detak Jantung"
                    R.id.favorites -> R.color.favorites to "Olahraga"
                    R.id.settings -> R.color.settings to "Obat-obatan"
                    else -> R.color.white to ""
                }
                val color = ContextCompat.getColor(context, option.first)
                container.colorAnimation(lastColor, color)
                lastColor = color
                title.text = option.second*/
                if (id==R.id.home)
                {
                    context.startActivity(Intent(context, DashboardActivity::class.java))


                }else if (id==R.id.heartbeat)
                {
                    context.startActivity(Intent(context, HeartBeatActivity::class.java))
                }else if (id==R.id.sport)
                {
                    context.startActivity(Intent(context, StepSportActivity::class.java))
                }else if (id==R.id.drug)
                {
                    context.startActivity(Intent(context, DrugUsageActivity::class.java))
                }else if (id==R.id.settings)
                {
                    context.startActivity(Intent(context, SettingActivity::class.java))
                }else if (id==R.id.about)
                {
                    context.startActivity(Intent(context, AboutActivity::class.java))
                }

            }
        })

        button.setOnClickListener {
            if (menu.isExpanded()) {
                TransitionManager.beginDelayedTransition(container, ChangeBounds())
                menu.collapse()
            } else {
                TransitionManager.beginDelayedTransition(container, ChangeBounds())
                menu.expand()
            }
        }

        button.applyWindowInsets(bottom = true)
    }


}