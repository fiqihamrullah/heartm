package com.heartm.heartbeat

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.loadingview.LoadingDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_form_drug_usage.*
import kotlinx.android.synthetic.main.activity_form_drug_usage.btnSave
import kotlinx.android.synthetic.main.activity_form_step_sport.*
import org.json.JSONException
import org.json.JSONObject

class FormStepSportActivity : AppCompatActivity() {

    private val URL_STEPSPORT =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "langkahpasien"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_step_sport)

        initToolbar()

        btnSave.setOnClickListener(View.OnClickListener { saveStep() })

    }


    fun saveStep()
    {

        val step = edStepCount.text.toString()



        if (step.equals(""))
        {
            Toasty.warning(this@FormStepSportActivity, "Data masih ada yag kosong!.", Toast.LENGTH_SHORT, true)
                .show()
        }  else {

            //Toast.makeText(getApplicationContext(),tags,Toast.LENGTH_LONG).show();

            val postparams = JSONObject()
            try
            {
                postparams.put("pasien_id",UserAccount.getID())
                postparams.put("jumlah_langkah",step)


            } catch (e: JSONException) {
                e.printStackTrace()
            }


            val dialog = LoadingDialog.get(this).show()

            val jsonObjReq = JsonObjectRequest(
                Request.Method.POST, URL_STEPSPORT, postparams,
                Response.Listener {
                    dialog.hide()
                    SweetAlertDialog(
                        this@FormStepSportActivity,
                        SweetAlertDialog.SUCCESS_TYPE
                    )
                        .setTitleText("Sukses!!")
                        .setContentText("Data berhasil tersimpan!!")
                        //  .setConfirmButtonBackgroundColor(R.color.extraDialogcolorPrimary)
                        .setConfirmButtonBackgroundColor(Color.argb(255, 73, 94, 123))
                        //  .setConfirmButtonBackgroundColor(Color.BLUE.darker())
                        .setConfirmClickListener {
                            it.hide()

                        }


                        .show()
                },
                Response.ErrorListener {
                    //Failure Callback
                    dialog.hide()
                })

            MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "postRequest_addStepSport")



        }

    }


    fun initToolbar()
    {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Langkah Olahraga Anda")
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
