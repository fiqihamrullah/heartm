package com.heartm.heartbeat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.loadingview.LoadingDialog
import com.google.gson.Gson
import com.heartm.heartbeat.models.Patient

import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val URL_LOGIN_POST =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "auth"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initComponent()





    }



    fun initComponent()
    {
        id_Signin_Button.setOnClickListener(View.OnClickListener {
             login()
        })


    }

    private fun registerSession(user: Patient,api_token : String)
    {
        UserAccount.setID(user.id.toString())
        UserAccount.setEmail(user.no_hp)
        UserAccount.setUserName(user.nama)
        UserAccount.setToken(api_token)



        val sm = SessionManager(this@LoginActivity)
        sm.createLoginSession(
            UserAccount.getID().toString(),
            UserAccount.getUsername().toString(),
            UserAccount.getEmail().toString(),
            api_token,
            "",
            UserAccount.getAccess()
        )

    }

    fun login()
    {

        val email = edPhoneNumber.getText().toString()
        val password = edPassword.getText().toString()
        if (email == "")
        {
            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Belum Lengkap!!")
                .setContentText("No HP Masih Kosong !!")
                .show()


        } else
        {

            val postparams = JSONObject()
            try {
                postparams.put("no_hp", email)
                postparams.put("password", password)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.i("URL ", URL_LOGIN_POST)

            val dialog = LoadingDialog.get(this).show()

            val jsonObjReq = JsonObjectRequest(
                Request.Method.POST, URL_LOGIN_POST, postparams,
                Response.Listener { response ->
                    //Success Callback
                    Log.i("HASIL POST ", response.toString())
                    try {
                        if (response.getString("status").equals("OK"))
                        {
                            val gson = Gson()

                            val user = gson.fromJson<Patient>(
                                response.getString("result"),
                                Patient::class.java!!
                            )




                            registerSession(user, response.getString("api_token"))


                            startActivity(Intent(this,DashboardActivity::class.java))
                        } else {

                            SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Ups..")
                                .setContentText(response.getString("message"))
                                .show()

                        }


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    dialog.hide()
                },
                Response.ErrorListener { error ->
                    //Failure Callback
                    dialog.hide()

                    if (error.message != null) {

                        SweetAlertDialog(this@LoginActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Ups..")
                            .setContentText(error.message)
                            .show()

                    }
                })

            MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "postRequest_login")

        }

    }

}
