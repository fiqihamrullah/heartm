package com.heartm.heartbeat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.loadingview.LoadingDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heartm.heartbeat.models.DrugUsage
import com.heartm.heartbeat.models.HeartBeatRecord
import com.heartm.heartbeat.models.WalkingSport
import com.heartm.heartbeat.notification.AlarmScheduler
import com.heartm.heartbeat.util.Menu
import com.heartm.heartbeat.util.MyDateConverter
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.activity_dashboard_actvity.*
import org.json.JSONException
import org.json.JSONObject

class DashboardActivity : AppCompatActivity()
{

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }


    private val URL_SUMMARY =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "summary?pasien_id=" + UserAccount.getID()



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_actvity)

        val menuNav = Menu(this@DashboardActivity)
        menuNav.initMenu(container,button,menu)

        loadSummary()

      //  AlarmScheduler.scheduleNextDrugTaken(this@DashboardActivity)
       // AlarmScheduler.scheduleDrinkInMorning(this@DashboardActivity)
        AlarmScheduler.cancelSchedule(this@DashboardActivity)



    }

    fun loadSummary()
    {
        val dialog = LoadingDialog.get(this).show()
        val jsonObjReq =
            StringRequest(
                Request.Method.GET, URL_SUMMARY,
                Response.Listener { response -> //Success Callback
                    Log.i("HASIL GET ", response)
                    dialog.hide()
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response)
                        val gson = Gson()

                        val mydate = MyDateConverter()

                        val heartbeat : HeartBeatRecord  = gson.fromJson<HeartBeatRecord>(jsonObject.getJSONObject("result").getString("rekam_jantung"),object:TypeToken<HeartBeatRecord?>(){}.type)
                        val fullDayOfMonth = mydate.convertfromLongDate(heartbeat.created_at, "dd MMM yyyy HH:ss")

                        tvTimeofHeartBeat.text = fullDayOfMonth
                        tvValue.text = heartbeat.bpm.toString()

                        if (heartbeat.bpm in 60..100)
                        {
                            tvStatusHeartBeat.setText("Sehat")
                        }else{
                            tvStatusHeartBeat.setText("Sakit")
                        }

                        val stepsport : WalkingSport  = gson.fromJson<WalkingSport>(jsonObject.getJSONObject("result").getString("langkah_pasien"),object:TypeToken<WalkingSport?>(){}.type)
                        val fullDayOfMonth2 = mydate.convertfromLongDate(stepsport.created_at, "dd MMM yyyy HH:ss")
                        tvTimeofStep.text = fullDayOfMonth2
                        tvStepCount.text = stepsport.jumlah_langkah.toString()

                        val drugusage : DrugUsage  = gson.fromJson<DrugUsage>(jsonObject.getJSONObject("result").getString("kontrol_obat"),object:TypeToken<DrugUsage?>(){}.type)
                        val nextdateofdrugtaken = mydate.convertfromShortDate(drugusage.tgl_ambil_selanjutnya, "dd MMM yyyy")
                        tvTimeofDrug.text = "Selanjutnya " +  nextdateofdrugtaken
                        tvNameOfDrug.text = drugusage.obat
                        val arrsplit : List<String> = drugusage.waktu_makan.split(",")
                        tvStatusOfDrug.text = arrsplit.size.toString() + "x / Hari"






                    } catch (ex: JSONException) {
                    }



                    dialog.hide()
                },
                Response.ErrorListener {
                    //Failure Callback
                })
        MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "getRequest_getSummary")

    }
}
