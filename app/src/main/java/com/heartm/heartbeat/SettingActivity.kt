package com.heartm.heartbeat


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.loadingview.LoadingDialog
import com.heartm.heartbeat.util.LocaleHelper.setLocale
import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONException
import org.json.JSONObject


class SettingActivity : AppCompatActivity() {

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val btnIndonesia by lazy { findViewById<TextView>(R.id.btnIndonesia) }
    private val btnEnglish by lazy { findViewById<TextView>(R.id.btnEnglish) }
    private val btnShowUpdateProfilePage by lazy { findViewById<TextView>(R.id.btnShowUpdatePage) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }

    private val URL_UPDATE_PASS: String =
        MyApplication.instance?.getResources()?.getString(R.string.online_url)
            .toString() + "pasien/password"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)



        btnChangePassword.setOnClickListener {
            showDialogInputPassword()
        }

        btnLogout.setOnClickListener {
            val sm = SessionManager(this@SettingActivity)
            sm.logout()
            this.finish()

        }


        val menuNav = Menu(this@SettingActivity)
        menuNav.initMenu(container,button,menu)


        btnIndonesia.setOnClickListener {
            var languagePref = "in"



            if (!languagePref.isEmpty())
            {
                setLocale(this@SettingActivity, languagePref)
                recreate()
            }

            Toasty.info(this@SettingActivity, "Sekarang , Apps Menggunakan Bahasa Indonesia!.", Toast.LENGTH_SHORT, true)
                .show()
        }


        btnEnglish.setOnClickListener {
            var languagePref = "en"



            if (!languagePref.isEmpty())
            {
                setLocale(this@SettingActivity, languagePref)
                recreate()
            }

            Toasty.info(this@SettingActivity, "Now, Apps Language In English!.", Toast.LENGTH_SHORT, true)
                .show()
        }

        btnShowUpdateProfilePage.setOnClickListener {
            startActivity(Intent(this,ViewWebFormActivity::class.java).putExtra("pil",3))
        }


    }


    private fun showDialogInputPassword() {
        val builder =
            AlertDialog.Builder(this@SettingActivity)
        builder.setTitle("Ganti Kata Laluan Anda")
        val viewInflated: View =
            LayoutInflater.from(this@SettingActivity).inflate(R.layout.dialog_change_password, null, false)
        val password =
            viewInflated.findViewById<View>(R.id.txtPassword) as EditText
        val repassword =
            viewInflated.findViewById<View>(R.id.txtRePassword) as EditText
        builder.setView(viewInflated)
        builder.setPositiveButton(
            android.R.string.ok
        ) { dialog, which ->
            val pass = password.text.toString()
            val repass = repassword.text.toString()
            if (pass == "") {
                Toasty.warning(
                    this@SettingActivity,
                    "Kata Laluan Kosong!.",
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            } else {
                if (pass.length > 5) {
                    if (repass == pass) {
                        changePassword(pass)
                    } else {
                        Toasty.warning(
                            this@SettingActivity,
                            "Kata Laluan Tidak Sama!.",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                } else {
                    Toasty.warning(
                        this@SettingActivity,
                        "Kata Laluan Terlalu Singkat!",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }


            //   withdraw(wdOrder);
        }
        builder.setNegativeButton(
            android.R.string.cancel
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun changePassword(pass: String) {


        //Toast.makeText(getApplicationContext(),tags,Toast.LENGTH_LONG).show();
        val postparams = JSONObject()
        try {
            postparams.put("id", UserAccount.getID())
            postparams.put("password", pass)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val dialog = LoadingDialog.get(this).show()
        val jsonObjReq =
            JsonObjectRequest(
                Request.Method.POST, URL_UPDATE_PASS, postparams,
                Response.Listener { //item.setInitCol(item.getColumnIndex());
                    val sessionManager = SessionManager(this@SettingActivity)
                    dialog.hide()
                    Toasty.info(
                        this@SettingActivity,
                        "Kata Laluan baru telah Anda tentukan!",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                },
                Response.ErrorListener {
                    //Failure Callback
                    dialog.hide()
                })
        MyApplication.instance?.addToRequestQueue(jsonObjReq, "postRequest_updatePass")
    }


}
