package com.heartm.heartbeat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.loadingview.LoadingDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heartm.heartbeat.adapters.WalkingSportAdapter
import com.heartm.heartbeat.models.WalkingSport
import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.activity_step_sport.*
import kotlinx.android.synthetic.main.activity_tes.*
import kotlinx.android.synthetic.main.activity_tes.fab
import org.json.JSONException
import org.json.JSONObject

class StepSportActivity : AppCompatActivity() {

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }

    private var walkingSportAdapter : WalkingSportAdapter? = null

    private val URL_STEPSPORT =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "langkahpasien?pasien_id=" + UserAccount.getID()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_sport)

        val menuNav = Menu(this@StepSportActivity)
        menuNav.initMenu(container,button,menu)

        fab.setOnClickListener { view ->
           // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
             //   .setAction("Action", null).show()
            startActivity(Intent(this, FormStepSportActivity::class.java))
        }

        init()

    }

    private fun init()
    {

        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this@StepSportActivity)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this@StepSportActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        loadStepSport()
    }


    private fun loadStepSport() {
        val dialog = LoadingDialog.get(this).show()
        val jsonObjReq =
            StringRequest(
                Request.Method.GET, URL_STEPSPORT,
                Response.Listener { response -> //Success Callback
                    Log.i("HASIL GET ", response)
                    dialog.hide()
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response)
                        val gson = Gson()
                        val stepSportList: ArrayList<WalkingSport> =
                            gson.fromJson<ArrayList<WalkingSport>>(
                                jsonObject.getString("result"),
                                object :
                                    TypeToken<List<WalkingSport?>?>() {}.type
                            )
                        walkingSportAdapter = WalkingSportAdapter(stepSportList)
                        recyclerView.setAdapter(walkingSportAdapter)
                    } catch (ex: JSONException) {
                    }



                    dialog.hide()
                },
                Response.ErrorListener {
                    //Failure Callback
                })
        MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "getRequest_getStepSport")
    }



}
