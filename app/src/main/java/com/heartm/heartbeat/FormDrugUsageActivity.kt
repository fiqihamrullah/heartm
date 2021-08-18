package com.heartm.heartbeat

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.loadingview.LoadingDialog
import com.heartm.heartbeat.fragments.DateTimePickerFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_form_drug_usage.*
import kotlinx.android.synthetic.main.activity_form_drug_usage.btnSave

import org.json.JSONException
import org.json.JSONObject

class FormDrugUsageActivity : AppCompatActivity()
{

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
        val obat = edDrugName.text.toString()

        val sbpagi : Boolean = swpagi.isChecked
        val sbsiang : Boolean = swsiang.isChecked
        val sbmalam : Boolean = swmalam.isChecked




        if (tgl.equals("") || obat.equals("") || tgl_next.equals(""))
        {
            Toasty.warning(this@FormDrugUsageActivity, "Data masih ada yag kosong!.", Toast.LENGTH_SHORT, true)
                .show()
        } else if (sbpagi==false && sbsiang==false && sbmalam==false)
        {
            Toasty.warning(this@FormDrugUsageActivity, "Waktu Pemakaian Obat belum ditentukan!.", Toast.LENGTH_SHORT, true)
                .show()
        } else
        {
            var selectedTimeOfDrugUsage : String =""
            if (sbpagi)
            {
                selectedTimeOfDrugUsage = "Pagi,"
            }

            if (sbsiang)
            {
                selectedTimeOfDrugUsage += "Siang,"
            }

            if (sbmalam)
            {
                selectedTimeOfDrugUsage += "Malam"
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
                            it.hide()
                            this.finish()

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
