package com.heartm.heartbeat

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.loadingview.LoadingDialog
import com.heartm.heartbeat.fragments.DateTimePickerFragment
import com.heartm.heartbeat.notification.AlarmScheduler
import com.heartm.heartbeat.util.MyDateConverter
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_form_drug_usage.*
import org.json.JSONException
import org.json.JSONObject


class FormDrugUsageActivity : AppCompatActivity()
{

     var resep=0;

    private val URL_KONTROLOBAT =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "kontrolobat"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_drug_usage)

        initToolbar()

        edTglInput.setOnClickListener(View.OnClickListener {

            showDatePicker(edTglInput)

        })

        edTglPengambilanBerikut.setOnClickListener {
            showDatePicker(edTglPengambilanBerikut)
        }

        btnSave.setOnClickListener(View.OnClickListener { saveDrugUsage() })

        btnAdd.setOnClickListener(View.OnClickListener { resetToAdd() })

        initSpinnerDrugNames()

        //btnAdd.isEnabled = false

        resep = intent.getIntExtra("resep",1)+1
        val tgl_ambil = intent.getStringExtra("tgl_ambil")
        if (tgl_ambil.isNotEmpty())
        {
            val mydate = MyDateConverter()
            val daymonth = mydate.convertfromShortDate(tgl_ambil, "dd/MM/yyyy")
            edTglInput.setText(daymonth)
            edTglInput.isEnabled = false
        }

        println("Resep Sekarang "  + resep.toString())


    }

    fun resetToAdd()
    {
        swpagi.isChecked = false
        swmalam.isChecked = false
        swsiang.isChecked = false

        edJumlah.setText("")
        btnAdd.visibility = View.GONE
        btnSave.visibility = View.VISIBLE

    }

    fun initSpinnerDrugNames()
    {
        var names = resources.getStringArray(R.array.obat)
        var aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        acDrugName.setAdapter(aa)

        acDrugName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> acDrugName.showDropDown()
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

    }


    fun showDatePicker(edHolder: EditText)
    {
        val newFragment = DateTimePickerFragment()
        newFragment.setHolder(edHolder)

        newFragment.show(supportFragmentManager, "date picker")
    }


    fun saveDrugUsage()
    {


        val tgl = edTglInput.text.toString()
        val tgl_next = edTglPengambilanBerikut.text.toString()
        val obat = acDrugName.text.toString()
        val jumlah = edJumlah.text.toString()
        val sbpagi : Boolean = swpagi.isChecked
        val sbsiang : Boolean = swsiang.isChecked
        val sbmalam : Boolean = swmalam.isChecked




        if (tgl.equals("") || obat.equals("") || tgl_next.equals("") || jumlah.equals(""))
        {
            Toasty.warning(this@FormDrugUsageActivity, "Data masih ada yang kosong!.", Toast.LENGTH_SHORT, true)
                .show()
        } else if (sbpagi==false && sbsiang==false && sbmalam==false)
        {
            Toasty.warning(this@FormDrugUsageActivity, "Waktu Pemakaian Obat belum ditentukan!.", Toast.LENGTH_SHORT, true)
                .show()
        } else
        {
            var bMorningDose:Boolean = false
            var bAfterNoonDose:Boolean = false
            var  bEveningDose : Boolean = false

            var selectedTimeOfDrugUsage : String =""
            if (sbpagi)
            {
                selectedTimeOfDrugUsage = "Pagi,"
                bMorningDose  = true
            }

            if (sbsiang)
            {
                selectedTimeOfDrugUsage += "Siang,"
                bAfterNoonDose = true
            }

            if (sbmalam)
            {
                selectedTimeOfDrugUsage += "Malam"
                bEveningDose = true
            }


            if (selectedTimeOfDrugUsage.endsWith(','))
            {
                selectedTimeOfDrugUsage =  selectedTimeOfDrugUsage.trimEnd(',')
            }


            //Toast.makeText(getApplicationContext(),tags,Toast.LENGTH_LONG).show();

            val postparams = JSONObject()
            try
            {
                postparams.put("pasien_id",UserAccount.getID())
                postparams.put("obat",obat)
                postparams.put("resep",resep)
                postparams.put("jumlah",jumlah)
                postparams.put("tgl_ambil_obat",tgl)
                postparams.put("tgl_ambil_obat_berikutnya",tgl_next)
                postparams.put("waktu_makan",selectedTimeOfDrugUsage)


            } catch (e: JSONException) {
                e.printStackTrace()
            }


            val dialog = LoadingDialog.get(this).show()

            val jsonObjReq = JsonObjectRequest(
                Request.Method.POST, URL_KONTROLOBAT, postparams,
                Response.Listener {
                    dialog.hide()

                    val sessmgr = SessionManager(this@FormDrugUsageActivity)
                    sessmgr.saveNextDateDrugTaken(tgl_next)
                    sessmgr.saveTimeofDrugUsage(selectedTimeOfDrugUsage)



                    SweetAlertDialog(
                        this@FormDrugUsageActivity,
                        SweetAlertDialog.SUCCESS_TYPE
                    )
                        .setTitleText("Sukses!!")
                        .setContentText("Data berhasil tersimpan!!")
                        //  .setConfirmButtonBackgroundColor(R.color.extraDialogcolorPrimary)
                        .setConfirmButtonBackgroundColor(Color.argb(255, 73, 94, 123))
                        //  .setConfirmButtonBackgroundColor(Color.BLUE.darker())
                        .setConfirmClickListener {

                           // AlarmScheduler.cancelSchedule(this@FormDrugUsageActivity)

                            Toasty.info(this@FormDrugUsageActivity, "Alarm Notifikasi Telah Diaktifkan!.", Toast.LENGTH_SHORT, true)
                                .show()

                            if (bMorningDose)  AlarmScheduler.scheduleDrinkInMorning(this@FormDrugUsageActivity)
                            if (bAfterNoonDose)  AlarmScheduler.scheduleDrinkInAfternoon(this@FormDrugUsageActivity)
                            if (bEveningDose)  AlarmScheduler.scheduleDrinkInEvening(this@FormDrugUsageActivity)

                            AlarmScheduler.scheduleNextDrugTaken(this@FormDrugUsageActivity)

                            it.hide()
                          //  this.finish()
                            btnSave.visibility = View.GONE
                            btnAdd.visibility = View.VISIBLE

                        }


                        .show()
                },
                Response.ErrorListener {
                    //Failure Callback
                    dialog.hide()
                })

            MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "postRequest_addDrugControl")



        }


    }



        fun initToolbar()
    {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Obat yang digunakan ")
        supportActionBar?.setSubtitle("Input Data dengan Benar")

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item?.itemId==android.R.id.home)
        {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


}
