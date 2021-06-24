package com.heartm.heartbeat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class DashboardActvity : AppCompatActivity()
{

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_actvity)

        val menuNav = Menu(this@DashboardActvity)
        menuNav.initMenu(container,button,menu)


    }
}
