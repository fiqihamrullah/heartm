package com.heartm.heartbeat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.github.loadingview.LoadingDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heartm.heartbeat.adapters.HeartBeatAdapter

import com.heartm.heartbeat.models.HeartBeatRecord

import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar


import kotlinx.android.synthetic.main.activity_heart_beat_history.*
import kotlinx.android.synthetic.main.activity_heart_beat_history.recyclerView

import org.json.JSONException
import org.json.JSONObject

class HeartBeatHistoryActivity : AppCompatActivity() {

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }

    private var heartBeatAdapter : HeartBeatAdapter? = null

    private val URL_HEARTBEAT =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "rekamjantung?pasien_id=" + UserAccount.getID()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_beat_history)

        val menuNav = Menu(this@HeartBeatHistoryActivity)
        menuNav.initMenu(container,button,menu)


        init()

    }

    private fun init()
    {

        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this@HeartBeatHistoryActivity)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this@HeartBeatHistoryActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        loadHeartPulseHistory()
    }


    private fun loadHeartPulseHistory() {
        val dialog = LoadingDialog.get(this).show()
        val jsonObjReq =
            StringRequest(
                Request.Method.GET, URL_HEARTBEAT,
                Response.Listener { response -> //Success Callback
                    Log.i("HASIL GET ", response)
                    dialog.hide()
                    var jsonObject: JSONObject? = null
                    try
                    {

                        jsonObject = JSONObject(response)
                        val gson = Gson()
                        val heartBeatList: ArrayList<HeartBeatRecord> =
                            gson.fromJson<ArrayList<HeartBeatRecord>>(
                                jsonObject.getString("result"),
                                object :
                                    TypeToken<List<HeartBeatRecord?>?>() {}.type
                            )

                        if (heartBeatList.size > 0)
                        {
                            heartBeatAdapter = HeartBeatAdapter(heartBeatList)
                            recyclerView.setAdapter(heartBeatAdapter)
                        }else{
                            recyclerView.visibility = View.GONE
                            animationView.visibility = View.VISIBLE
                        }

                    }
                    catch (ex: JSONException)
                    {
                    }



                    dialog.hide()
                },
                Response.ErrorListener {
                    //Failure Callback
                })
        MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "getRequest_getHeartPulseHistory")
    }


}
