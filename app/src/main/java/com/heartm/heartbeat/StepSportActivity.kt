package com.heartm.heartbeat


import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.github.loadingview.LoadingDialog
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heartm.heartbeat.adapters.WalkingSportAdapter
import com.heartm.heartbeat.fragments.DateTimePickerFragment
import com.heartm.heartbeat.models.WalkingSport
import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.kal.rackmonthpicker.RackMonthPicker
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_step_sport.*
import kotlinx.android.synthetic.main.activity_tes.fab
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt

class StepSportActivity : AppCompatActivity() {

    private val MODE_DAY= 1001
    private val MODE_WEEK= 1002
    private val MODE_MONTH= 1003

    private var textToShare:String?= null


    private val REQUESTCODE_ADDSTEP = 1
    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }
    private val chipStatus by lazy { findViewById<ChipGroup>(R.id.chpgrpStatus) }
    private val nts_center by lazy { findViewById<NavigationTabStrip>(R.id.nts_center)}
    private val chart by lazy { findViewById<BarChart>(R.id.chart)}
    private val imgVFilter by lazy { findViewById<ImageView>(R.id.imgVFilter)}

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
            startActivityForResult(Intent(this, FormStepSportActivity::class.java),REQUESTCODE_ADDSTEP)
        }



        init()

        fab_share.setOnClickListener(View.OnClickListener
        {

            if (textToShare.isNullOrBlank())
            {

                Toasty.warning(this@StepSportActivity, getString(R.string.no_data_to_share), Toast.LENGTH_SHORT, true)
                    .show()

            }else
            {
                val intentShare = Intent(Intent.ACTION_SEND)
                intentShare.setType("text/plain")
                intentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sport))
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

        imgVFilter.setOnClickListener(View.OnClickListener {


            if (chipStatus.checkedChipId==R.id.chipDay)
            {
                filterByDateAndWeek(MODE_DAY)
            }else if (chipStatus.checkedChipId==R.id.chipWeek)
            {
                filterByDateAndWeek(MODE_WEEK)
            }else if (chipStatus.checkedChipId==R.id.chipMonth)
            {
                filterByMonth()
            }


        })

    }

    fun filterByDateAndWeek(mode:Int)
    {

        val newFragment = DateTimePickerFragment()
        newFragment.setOnDateSetListner(DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val strdate = view.dayOfMonth.toString() + "/" + (view.month + 1) + "/" + view.year
            if (mode==MODE_DAY)
            {
                Toasty.info(StepSportActivity@ this, "Menampilkan Data di Tanggal " + strdate).show()
                loadStepSport(mode,strdate)
            }else if (mode==MODE_WEEK)
            {
                Toasty.info(StepSportActivity@ this, "Menampilkan Data Minggu diawali di tanggal " + strdate).show()
                loadStepSport(mode,strdate)

            }

        })
        newFragment.show(supportFragmentManager, "date picker")

    }


    fun filterByMonth()
    {
        RackMonthPicker(this)
            .setLocale(Locale.ENGLISH)
            .setColorTheme(R.color.drugs)
            .setPositiveButton { month, startDate, endDate, year, monthLabel ->
                val strdate = "1/" + (month ) + "/" + year
                Toasty.info(StepSportActivity@ this,"Menampilkan Data Bulan " + monthLabel).show()
                loadStepSport(MODE_MONTH,strdate)

            }
            .setNegativeButton { }.show()


    }

    fun createTextToShare(stepSportList: ArrayList<WalkingSport>)
    {
        val strinB = StringBuilder()
        for(walk in stepSportList)
        {
            strinB.appendln("[[" + walk.created_at  + "]] Jumlah " + walk.total_per_hari + " Langkah")
        }

        textToShare = strinB.toString()



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUESTCODE_ADDSTEP)
        {
            chipStatus.check(R.id.chipDay)
        }
    }

    private fun setDataToChart(jsonArray: JSONArray)
    {
        val start = 1f
        val values = java.util.ArrayList<BarEntry>()
        val months = arrayOfNulls<String>(jsonArray.length())
        val xAxis: XAxis = chart.getXAxis()
        var maxYVal : Int = 0
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        for (i in 0 until jsonArray.length()) {
            var jsonObject: JSONObject? = null
            try
            {
                jsonObject = jsonArray.getJSONObject(i)

                for(key in jsonObject.keys())
                {
                    months[i] = key
                    val value  = jsonObject.getInt(key).toFloat()
                    values.add(BarEntry(i.toFloat(), value))

                    if (maxYVal < value) maxYVal = value.toInt()

                }




            } catch (ex: JSONException) {
            }
        }

        var yAxis = chart.getAxisLeft()
        yAxis.axisMaximum = maxYVal.toFloat()

        val set1: BarDataSet
        if (chart.getData() != null &&
            chart.getData().getDataSetCount() > 0
        ) {
            set1 = chart.getData().getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart.getData().notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "Grafik")
            set1.setDrawIcons(false)


            val startColor: Int = ContextCompat.getColor(this, R.color.material_blue_grey_80)
            val endColor: Int = ContextCompat.getColor(this, android.R.color.holo_blue_bright)
            set1.setGradientColor(startColor, endColor)


            val dataSets = java.util.ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            //  data.setValueTypeface(tfLight);
            data.barWidth = 0.9f
            chart.setData(data)
        }


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


        chipStatus.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, i -> //Toast.makeText(getActivity(),String.valueOf(i),Toast.LENGTH_LONG).show();
            if (i == R.id.chipDay)
            {
                nts_center.visibility = View.GONE
                chart.visibility = View.GONE
                loadStepSport(MODE_DAY,"")

            } else if (i == R.id.chipWeek)
            {
                nts_center.visibility = View.VISIBLE
                nts_center.setTabIndex(0,true)
                nts_center.onTabStripSelectedIndexListener.onStartTabSelected("",0)
                loadStepSport(MODE_WEEK,"")

            } else if (i == R.id.chipMonth)
            {
                nts_center.visibility = View.VISIBLE
                nts_center.setTabIndex(0,true)
                nts_center.onTabStripSelectedIndexListener.onStartTabSelected("",0)
                loadStepSport(MODE_MONTH,"")
            }

        })

        chipStatus.check(R.id.chipDay)



        nts_center.onTabStripSelectedIndexListener = object:NavigationTabStrip.OnTabStripSelectedIndexListener{
            override fun onStartTabSelected(title: String?, index: Int)
            {


                if (index==0)
                {
                    chart.visibility = View.GONE
                  //  recyclerView.visibility = View.VISIBLE


                }else {
                    chart.visibility = View.VISIBLE
                   // recyclerView.visibility = View.GONE
                }
            }

            override fun onEndTabSelected(title: String?, index: Int)
            {

            }
        }

        nts_center.setTabIndex(0, true)



    }

    class MyCustomValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return if (value == 0f) {
                ""
            } else {
                value.roundToInt().toString()
            }
        }
    }

    private fun initChart(mode:Int)
    {
        chart.setBackgroundResource(R.color.grey_medium)


        chart.setDrawValueAboveBar(false)

        chart.getDescription().setEnabled(false)
        chart.setPinchZoom(false)
        chart.setClipValuesToContent(true);

        var xAxis: XAxis
        run {
            // // X-Axis Style // //
            xAxis = chart.getXAxis()
          //  xAxis.textColor = Color.WHITE
            xAxis.position =  XAxis.XAxisPosition.BOTTOM
            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f)
            //  xAxis.setCenterAxisLabels(true);
            //  xAxis.setAvoidFirstLastClipping(true);
            xAxis.setGranularity(1f) // only intervals of 1 day
            if (mode==MODE_WEEK)
            {
                xAxis.setLabelCount(7)
            }else if (mode==MODE_MONTH)
            {
                xAxis.setLabelCount(10)
            }
            xAxis.labelRotationAngle = 30f
           // xAxis.spaceMax = 0.1f
           // xAxis.spaceMin = 0.05f
            xAxis.valueFormatter = MyCustomValueFormatter()

        }
        var yAxis: YAxis
        run {
            // // Y-Axis Style // //
            yAxis = chart.getAxisLeft()

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false)

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f)

            // axis range
            yAxis.axisMaximum = 500f
            yAxis.axisMinimum = 0f
        }
        chart.animateXY(2000, 2000)
    }




    private fun loadStepSport( mode:Int,filter:String)
    {

        var url : String = ""

        initChart(mode)

        url = when(mode)
        {
            (MODE_DAY) ->  URL_STEPSPORT.plus("&mode=day")
            (MODE_WEEK) -> URL_STEPSPORT.plus("&mode=week")
            (MODE_MONTH) ->  URL_STEPSPORT.plus("&mode=month")
            else -> URL_STEPSPORT

        }

        if (filter.isNotEmpty())
        {
            url = url.plus("&filter=").plus(filter)
        }

        System.out.println("url " + url)

        val dialog = LoadingDialog.get(this).show()
        val jsonObjReq =
            StringRequest(
                Request.Method.GET, url,
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


                        createTextToShare(stepSportList)

                        if (mode==MODE_WEEK)
                        {
                            setDataToChart(jsonObject.getJSONArray("week_period"))
                        }else if (mode==MODE_MONTH)
                        {
                            setDataToChart(jsonObject.getJSONArray("month_period"))
                        }

                        System.out.println("yaaah di luar................")

                     if (stepSportList.size > 0)
                     {
                         System.out.println("Masukkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
                         animationView.visibility =  View.GONE
                         recyclerView.visibility = View.VISIBLE
                        walkingSportAdapter = WalkingSportAdapter(stepSportList)
                        walkingSportAdapter?.setOnItemClickListener(object:WalkingSportAdapter.OnItemClickListener{
                            override fun onItemClick(
                                view: View?,
                                obj: WalkingSport?,
                                position: Int
                            ) {
                                startActivityForResult(Intent(this@StepSportActivity, FormStepSportActivity::class.java),REQUESTCODE_ADDSTEP)
                            }
                        })

                        recyclerView.setAdapter(walkingSportAdapter)
                    }else{
                        recyclerView.visibility = View.GONE
                        animationView.visibility = View.VISIBLE
                    }


                    } catch (ex: JSONException) {
                        System.out.println("Hmmm ado error " + ex.message)

                    }



                    dialog.hide()
                },
                Response.ErrorListener {
                    //Failure Callback
                })
        MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "getRequest_getStepSport")
    }



}
