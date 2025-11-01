package com.heartm.heartbeat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.github.loadingview.LoadingDialog
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heartm.heartbeat.adapters.*
import com.heartm.heartbeat.databinding.ActivityDrugUsageBinding
import com.heartm.heartbeat.models.*
import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import es.dmoral.toasty.Toasty
import org.json.JSONException
import org.json.JSONObject

class DrugUsageActivity : AppCompatActivity()
{

    private val MODE_DAY= 1001
    private val MODE_WEEK= 1002
    private val MODE_MONTH= 1003


    private val REQUESTCODE_ADDDRUGUSAGE=1

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }

    private val chipStatus by lazy { findViewById<ChipGroup>(R.id.chpgrpStatus) }

    private var initResep = 0
    private var tgl_pengambilan = ""


    private var drugUsageAdapter : DrugUsageAdapter? = null
    private var drugReceiptAdapter : DrugReceiptAdapter? = null
    private var drugUsageTakenDateAdapter : DrugTakenDateAdapter? = null

    private var textToShare:String?= null

    private lateinit var binding: ActivityDrugUsageBinding;

    private val URL_KONTROLOBAT =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "kontrolobat?pasien_id=" + UserAccount.getID()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrugUsageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menuNav = Menu(this@DrugUsageActivity)
        menuNav.initMenu(container,button,menu)

        binding.fab.setOnClickListener { view ->
            // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //   .setAction("Action", null).show()
            startActivityForResult(Intent(this, FormDrugUsageActivity::class.java).putExtra("resep",initResep).putExtra("tgl_ambil",tgl_pengambilan),REQUESTCODE_ADDDRUGUSAGE)
        }

        init()




        binding.fab_share.setOnClickListener(View.OnClickListener
        {

            if (textToShare.isNullOrBlank())
            {

                Toasty.warning(this@DrugUsageActivity, getString(R.string.no_data_to_share), Toast.LENGTH_SHORT, true)
                    .show()

            }else
            {
                val intentShare = Intent(Intent.ACTION_SEND)
                intentShare.setType("text/plain")
                intentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.medicine))
                intentShare.putExtra(
                    Intent.EXTRA_TEXT,
                    textToShare
                )

                startActivity(
                    Intent.createChooser(
                        intentShare,
                        getString(R.string.share_your_data)
                    )
                )
            }
        })

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

        /*
        chipStatus.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, i -> //Toast.makeText(getActivity(),String.valueOf(i),Toast.LENGTH_LONG).show();
            if (i == R.id.chipDay)
            {
                loadDrugTakenDateUsage(MODE_DAY)
            } else if (i == R.id.chipWeek)
            {
                loadDrugTakenDateUsage(MODE_WEEK)
            } else if (i == R.id.chipMonth)
            {
                loadDrugTakenDateUsage(MODE_MONTH)
            }

        })*/

        loadDrugTakenDateUsage(MODE_DAY)

    //    chipStatus.check(R.id.chipDay)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUESTCODE_ADDDRUGUSAGE)
        {
            loadDrugTakenDateUsage(MODE_DAY)
        }
    }


    fun createTextToShare( drugTakenDate: DrugTakenDate)
    {
        val strinB = StringBuilder()

        strinB.appendln("Tanggal :" + drugTakenDate.tgl_pengambilan_obat)
        strinB.appendln("Jumlah :" + drugTakenDate.jumlah_obat.toString())
        strinB.appendln("----------------------------")

        for(obat in drugTakenDate.obat!!)
        {
            strinB.appendln(obat.obat + " Berjumlah " + obat.jumlah + " dimakan Pada Waktu " +  obat.waktu_makan)
        }

        textToShare = strinB.toString()



    }

    private fun loadDrugTakenDateUsage(mode:Int)
    {

        var url : String = ""

        when(mode)
        {
            (MODE_DAY) -> url = URL_KONTROLOBAT.plus("&mode=day")
            (MODE_WEEK) -> url = URL_KONTROLOBAT.plus("&mode=week")
            (MODE_MONTH) -> url = URL_KONTROLOBAT.plus("&mode=month")

        }

        System.out.println("url " + url)

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
                        val drugUsageTakenDateList: ArrayList<DrugTakenDate> =
                            gson.fromJson<ArrayList<DrugTakenDate>>(
                                jsonObject.getString("result"),
                                object :
                                    TypeToken<List<DrugTakenDate?>?>() {}.type
                            )

                        if (drugUsageTakenDateList.size > 0)
                        {

                            drugUsageTakenDateAdapter = DrugTakenDateAdapter(drugUsageTakenDateList)
                            recyclerView.setAdapter(drugUsageTakenDateAdapter)

                            drugUsageTakenDateAdapter?.setOnItemClickListener(object:DrugTakenDateAdapter.OnItemClickListener{
                                override fun onItemClick(
                                    view: View?,
                                    obj: DrugTakenDate?,
                                    position: Int
                                ) {

                                        tgl_pengambilan =  obj?.tgl_pengambilan_obat.orEmpty()
                                        // drugUsageAdapter = DrugUsageAdapter(obj?.obat!!)
                                        //recyclerView.setAdapter(drugUsageAdapter)
                                        var items: ArrayList<DrugReceipt>

                                        items =ArrayList<DrugReceipt>()

                                        initResep=1

                                        var dr = DrugReceipt(initResep)

                                        for(obat in obj?.obat!!)
                                        {

                                            if (initResep!==obat.resep)
                                            {
                                                items.add(dr)
                                                initResep++
                                                dr = DrugReceipt(initResep)
                                            }


                                            dr.addObat(obat)



                                           // println("Obat " + obat.obat)


                                        }

                                         items.add(dr)

                                         println("Ukuran " + dr.getSize())


                                        drugReceiptAdapter = DrugReceiptAdapter(items)
                                        recyclerView.setAdapter(drugReceiptAdapter)
                                        fab_share.visibility = View.VISIBLE

                                        createTextToShare(obj)



                                }

                            })


                        }else{
                            recyclerView.visibility = View.GONE
                            animationView.visibility = View.VISIBLE
                        }

                        initResep =0

                        tgl_pengambilan = ""

                    } catch (ex: JSONException) {
                    }



                    dialog.hide()
                },
                Response.ErrorListener {
                    //Failure Callback
                })
        MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "getRequest_getDrugTakenDate")
    }


    private fun loadDrugUSage(json:String)
    {
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
