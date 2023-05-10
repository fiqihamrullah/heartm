package com.heartm.heartbeat

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.loadingview.LoadingDialog
import com.heartm.heartbeat.util.MyDateConverter
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

        init()


       edStepCount.addTextChangedListener(object : TextWatcher{
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

           }
           override fun afterTextChanged(s: Editable?) {

           }
           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
           {

               val step = s.toString()
               if (!step.equals(""))
               {
                   val sessmgr = SessionManager(this@FormStepSportActivity)

                   val itarget = if (sessmgr.getStepTarget()>0) sessmgr.getStepTarget() else edTargetCount.text.toString().toInt()

                   val sisa = itarget - (sessmgr.getTotalStep() + step.toInt())
                   if (sisa >= 0)
                   {
                       tvStepRest.text = sisa.toString()
                   }

               }



           }
       })

        btnSave.setOnClickListener(View.OnClickListener { saveStep() })

    }


    fun init()
    {
        val sessmgr = SessionManager(this@FormStepSportActivity)

         edTargetCount.setText(sessmgr.getStepTarget().toString())

        val sisa = sessmgr.getStepTarget() - sessmgr.getTotalStep()
        if (sisa >= 0) {
            tvStepRest.text = sisa.toString()
        }
    }


    fun saveStep()
    {

        val step = edStepCount.text.toString()
        val target = edTargetCount.text.toString()



        if (step.equals(""))
        {
            Toasty.warning(this@FormStepSportActivity, "Data masih ada yang kosong!.", Toast.LENGTH_SHORT, true)
                .show()
        } else if (target.equals("0"))
        {
            Toasty.warning(this@FormStepSportActivity, "Data target kosong!.", Toast.LENGTH_SHORT, true)
                .show()
        } else{

            //Toast.makeText(getApplicationContext(),tags,Toast.LENGTH_LONG).show();

            val sessmgr = SessionManager(this@FormStepSportActivity)
            val postparams = JSONObject()
            try
            {
                val totstep = Integer.parseInt(step) + sessmgr.getTotalStep()
                postparams.put("pasien_id",UserAccount.getID())
                postparams.put("jumlah_langkah",step)
                postparams.put("total_per_hari",totstep)
                postparams.put("jumlah_target",target)


            } catch (e: JSONException)
            {
                e.printStackTrace()
            }


            val dialog = LoadingDialog.get(this).show()

            val jsonObjReq = JsonObjectRequest(
                Request.Method.POST, URL_STEPSPORT, postparams,
                Response.Listener {
                    dialog.hide()

                    val mydate = MyDateConverter()
                    val currentdate = mydate.convertfromLongDate( it.getJSONObject("result").getString("created_at") , "dd/MM/yyyy")
                    if (sessmgr.getLastStepSaved().equals(currentdate))
                    {
                        sessmgr.saveStep(Integer.parseInt(step))
                    }else{
                        sessmgr.saveNewStep(Integer.parseInt(step))
                        sessmgr.saveLastStepSaved(currentdate.orEmpty())
                    }
                    sessmgr.saveStepTarget(target.toInt())






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

                            this.finish()

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item?.itemId==android.R.id.home)
        {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }




}
