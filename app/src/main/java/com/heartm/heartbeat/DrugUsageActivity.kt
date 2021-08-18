package com.heartm.heartbeat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.loadingview.LoadingDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heartm.heartbeat.adapters.DrugUsageAdapter
import com.heartm.heartbeat.adapters.WalkingSportAdapter
import com.heartm.heartbeat.models.DrugUsage
import com.heartm.heartbeat.models.WalkingSport
import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.activity_heart_beat_history.*
import kotlinx.android.synthetic.main.activity_step_sport.*
import kotlinx.android.synthetic.main.activity_step_sport.animationView
import kotlinx.android.synthetic.main.activity_step_sport.recyclerView
import kotlinx.android.synthetic.main.activity_tes.*
import kotlinx.android.synthetic.main.activity_tes.fab
import org.json.JSONException
import org.json.JSONObject

class DrugUsageActivity : AppCompatActivity()
{
    private val REQUESTCODE_ADDDRUGUSAGE=1

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }


    private var drugUsageAdapter : DrugUsageAdapter? = null


    private val URL_KONTROLOBAT =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "kontrolobat?pasien_id=" + UserAccount.getID()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drug_usage)

        val menuNav = Menu(this@DrugUsageActivity)
        menuNav.initMenu(container,button,menu)

        fab.setOnClickListener { view ->
            // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //   .setAction("Action", null).show()
            startActivityForResult(Intent(this, FormDrugUsageActivity::class.java),REQUESTCODE_ADDDRUGUSAGE)
        }

        init()

    }

    private fun init()
    {

        // use a linear layout manager
        val mLayoutManager = LinearLayoutManager(this@DrugUsageActivity)
        recyclerView.setLayoutManager(mLayoutManager)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this@DrugUsageActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        loadDrugUSage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUESTCODE_ADDDRUGUSAGE)
        {
            loadDrugUSage()
        }
    }


    private fun loadDrugUSage() {
        val dialog = LoadingDialog.get(this).show()
        val jsonObjReq =
            StringRequest(
                Request.Method.GET, URL_KONTROLOBAT,
                Response.Listener { response -> //Success Callback
                    Log.i("HASIL POST ", response)
                    dialog.hide()
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response)
                        val gson = Gson()
                        val drugUsageList: ArrayList<DrugUsage> =
                            gson.fromJson<ArrayList<DrugUsage>>(
                                jsonObject.getString("result"),
                                object :
                                    TypeToken<List<DrugUsage?>?>() {}.type
                            )

                         if (drugUsageList.size > 0)
                         {

                            drugUsageAdapter = DrugUsageAdapter(drugUsageList)
                            recyclerView.setAdapter(drugUsageAdapter)
                        }else{
                            recyclerView.visibility = View.GONE
                            animationView.visibility = View.VISIBLE
                        }

                    } catch (ex: JSONException) {
                    }



                    dialog.hide()
                },
                Response.ErrorListener {
                    //Failure Callback
                })
        MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "getRequest_getDrug")
    }
}
