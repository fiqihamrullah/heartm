package com.heartm.heartbeat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.loadingview.LoadingDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heartm.heartbeat.databinding.ActivityDashboardActvityBinding
import com.heartm.heartbeat.models.DrugUsage
import com.heartm.heartbeat.models.HeartBeatRecord
import com.heartm.heartbeat.models.WalkingSport
import com.heartm.heartbeat.notification.AlarmScheduler
import com.heartm.heartbeat.util.Menu
import com.heartm.heartbeat.util.MyDateConverter
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class DashboardActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityDashboardActvityBinding

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val tvUser by lazy { findViewById<TextView>(R.id.tvUser) }

    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }


    private val URL_SUMMARY =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "summary?pasien_id=" + UserAccount.getID()



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvUser.text = UserAccount.getUsername()


        val menuNav = Menu(this@DashboardActivity)
        menuNav.initMenu(container,button,menu)

        loadSummary()
        initDoctor()

        binding.imgVBtnSendMsg.setOnClickListener(View.OnClickListener {
            var phone: String = UserAccount.getDoctorPhoneNumber().toString()
          //  phone = phone.substring(1)
            phone = "+$phone"
            // Toast.makeText(getContext(),phone,Toast.LENGTH_LONG).show();
            // Toast.makeText(getContext(),phone,Toast.LENGTH_LONG).show();
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://api.whatsapp.com/send?phone=$phone&text=Halo Dok"
                    )
                )
            )
        })

       //AlarmScheduler.scheduleNextDrugTaken(this@DashboardActivity)
       //  AlarmScheduler.scheduleDrinkInMorning(this@DashboardActivity)
        AlarmScheduler.scheduleDailyUsage(this@DashboardActivity)
      // AlarmScheduler.cancelSchedule(this@DashboardActivity)



    }

    fun initDoctor()
    {
        binding.tvDoctorName.setText(UserAccount.getDoctorName())
    }

    fun loadSummary()
    {
        System.out.println(URL_SUMMARY)
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

                        if (!jsonObject.getJSONObject("result").isNull("rekam_jantung"))
                        {

                            val heartbeat: HeartBeatRecord = gson.fromJson<HeartBeatRecord>(
                                jsonObject.getJSONObject("result").getString("rekam_jantung"),
                                object : TypeToken<HeartBeatRecord?>() {}.type
                            )
                            val fullDayOfMonth = mydate.convertfromLongDate(
                                heartbeat.created_at,
                                "dd MMM yyyy HH:ss"
                            )

                            binding.tvTimeofHeartBeat.text = fullDayOfMonth
                            binding.tvValue.text = heartbeat.bpm.toString()

                            if (heartbeat.bpm in 60..100) {
                                binding.tvStatusHeartBeat.setText(getString(R.string.healthy))
                            } else {
                                binding.tvStatusHeartBeat.setText(getString(R.string.sick))
                            }

                        }


                        if (!jsonObject.getJSONObject("result").isNull("langkah_pasien"))
                        {
                            val stepsport: WalkingSport = gson.fromJson<WalkingSport>(
                                jsonObject.getJSONObject("result").getString("langkah_pasien"),
                                object : TypeToken<WalkingSport?>() {}.type
                            )
                            val fullDayOfMonth2 = mydate.convertfromLongDate(
                                stepsport.created_at,
                                "dd MMM yyyy HH:ss"
                            )
                            binding.tvTimeofStep.text = fullDayOfMonth2
                            binding.tvStepCount.text = stepsport.jumlah_langkah.toString()
                        }


                        if (!jsonObject.getJSONObject("result").isNull("kontrol_obat")) {

                            val drugusage: DrugUsage = gson.fromJson<DrugUsage>(
                                jsonObject.getJSONObject("result").getString("kontrol_obat"),
                                object : TypeToken<DrugUsage?>() {}.type
                            )
                            val nextdateofdrugtaken = mydate.convertfromShortDate(
                                drugusage.tgl_ambil_selanjutnya,
                                "dd MMM yyyy"
                            )
                            binding.tvTimeofDrug.text = String.format(
                                Locale.getDefault(),
                                " %s %s",
                                getString(R.string.next_on),
                                nextdateofdrugtaken
                            )
                            binding.tvNameOfDrug.text = drugusage.obat
                            val arrsplit: List<String> = drugusage.waktu_makan.split(",")
                            binding.tvStatusOfDrug.text = String.format(
                                Locale.getDefault(),
                                "%sx/ %s",
                                arrsplit.size.toString(),
                                getString(R.string.per_day)
                            )
                            binding.tvAmount.text = drugusage.jumlah.toString() + " Pcs"
                        }






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


