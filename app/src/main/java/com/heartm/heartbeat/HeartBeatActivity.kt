package com.heartm.heartbeat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.loadingview.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.heartm.heartbeat.detector.CameraService
import com.heartm.heartbeat.detector.OutputAnalyzer
import com.heartm.heartbeat.util.Menu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_form_step_sport.*
import kotlinx.android.synthetic.main.activity_heart_beat.*
import org.json.JSONException
import org.json.JSONObject


class HeartBeatActivity : AppCompatActivity()
{

    private  var analyzer: OutputAnalyzer? = null

    private val REQUEST_CODE_CAMERA = 0
    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
    private val labelIndicator by lazy { findViewById<TextView>(R.id.tvLabel) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }

    private val justShared = false

    private val URL_HEARTPULSE =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_url) + "rekamjantung"

    @SuppressLint("HandlerLeak")
    private val mainHandler: Handler =
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == HeartBeatActivity.MESSAGE_UPDATE_REALTIME)
                {
                    (findViewById<View>(R.id.tvLog) as TextView).text = msg.obj.toString()
                }
                if (msg.what == HeartBeatActivity.MESSAGE_UPDATE_FINAL)
                {
                    val bpm = msg.obj.toString()
                    val ibpm = Integer.parseInt(bpm)

                    (findViewById<View>(R.id.tvBPM) as TextView).setText(bpm + " BPM")

                    var status = ""
                    labelIndicator.visibility = View.VISIBLE

                    if (ibpm in 60..100)
                    {
                        status = "Sehat"
                        labelIndicator.setBackgroundResource(R.drawable.green_rounded_corner)

                    }else {
                        status = "Sakit"
                        labelIndicator.setBackgroundResource(R.drawable.rounded_corner)
                    }

                    labelIndicator.text = status




                    saveHeartPulse(bpm)


                }
                if (msg.what == HeartBeatActivity.MESSAGE_CAMERA_NOT_AVAILABLE)
                {
                    Log.println(Log.WARN, "camera", msg.obj.toString())
                    (findViewById<View>(R.id.edHint) as TextView).setText(
                        R.string.camera_not_found
                    )
                    analyzer!!.stop()
                }
            }
        }

    private val cameraService: CameraService = CameraService(this, mainHandler)

    override fun onResume()
    {
        super.onResume()


        analyzer = OutputAnalyzer(
            this@HeartBeatActivity,
            findViewById<View>(R.id.graphTextureView) as TextureView,
            mainHandler
        )

        val cameraTextureView = findViewById<TextureView>(R.id.textureView2)
        val previewSurfaceTexture = cameraTextureView.surfaceTexture

        // justShared is set if one clicks the share button.
        if (previewSurfaceTexture != null && !justShared) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            val previewSurface = Surface(previewSurfaceTexture)

            // show warning when there is no flash
            if (!this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Snackbar.make(
                    findViewById(R.id.lllayout),
                    getString(R.string.noFlashWarning),
                    Snackbar.LENGTH_LONG
                ).show()
            }


            cameraService.start(previewSurface)
            analyzer?.measurePulse(cameraTextureView, cameraService)
        }
    }

    override fun onPause() {
        super.onPause()
        cameraService.stop()
        if (analyzer != null) analyzer?.stop()
        analyzer = OutputAnalyzer(
            this,
            findViewById<View>(R.id.graphTextureView) as TextureView,
            mainHandler
        )
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_beat)

        initMenu()


        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA
        )

        btnShowHistory.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, HeartBeatHistoryActivity::class.java))
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {

                Snackbar.make(
                    findViewById(R.id.lllayout),
                    getString(R.string.cameraPermissionRequired),
                    Snackbar.LENGTH_LONG
                ).show()

            }
        }
    }

    fun initMenu()
    {

        val menuNav = Menu(this@HeartBeatActivity)
        menuNav.initMenu(container,button,menu)

    }


    fun saveHeartPulse(bpm : String)
    {



            //Toast.makeText(getApplicationContext(),tags,Toast.LENGTH_LONG).show();

            val postparams = JSONObject()
            try
            {
                postparams.put("pasien_id",UserAccount.getID())
                postparams.put("bpm",bpm)


            } catch (e: JSONException) {
                e.printStackTrace()
            }


            val dialog = LoadingDialog.get(this).show()

            val jsonObjReq = JsonObjectRequest(
                Request.Method.POST, URL_HEARTPULSE, postparams,
                Response.Listener {
                    dialog.hide()
                    SweetAlertDialog(
                        this@HeartBeatActivity,
                        SweetAlertDialog.SUCCESS_TYPE
                    )
                        .setTitleText("Sukses!!")
                        .setContentText("Data BPM Anda berhasil tersimpan!!")
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

            MyApplication.Companion.instance?.addToRequestQueue(jsonObjReq, "postRequest_addHeartPulse")



    }

    companion object {
        const val MESSAGE_UPDATE_REALTIME = 1
        const val MESSAGE_UPDATE_FINAL = 2
        const val MESSAGE_CAMERA_NOT_AVAILABLE = 3
    }
}
