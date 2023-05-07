package com.heartm.heartbeat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_heart_beat.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class HeartBeatActivity : AppCompatActivity()
{

    private  var analyzer: OutputAnalyzer? = null

    private val REQUEST_CODE_CAMERA = 0
    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val title by lazy { findViewById<TextView>(R.id.title) }
   // private val labelIndicator by lazy { findViewById<TextView>(R.id.tvLabel) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }
    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }

    private var textToShare:String?= null

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


                    showDialogResult(bpm,ibpm)



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


        /*
        labelIndicator.setOnClickListener(View.OnClickListener {
            if (labelIndicator.text.equals(getString(R.string.healthy)))
            {
                val content = String.format(Locale.getDefault(),"%s %s 60 Bpm & 100 Bpm",getString(R.string.healthy),"Antara")

                SweetAlertDialog(this@HeartBeatActivity, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Selamat")
                    .setContentText(content)
                    .show()
            }else
            {
                val content = String.format(Locale.getDefault(),"%s %s 60 Bpm",getString(R.string.sick),"Dibawah")

                SweetAlertDialog(this@HeartBeatActivity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Ups!")
                    .setContentText(content)
                    .show()

            }
        })*/

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


    fun getStatus(ibpm:Int):String
    {
        var status = ""
        val usia = UserAccount.getUsia()
        if (UserAccount.getGender()=="L")
        {
            if (usia in 18..25)
            {
                    status= when(ibpm)
                    {
                        in  49..55 -> getString(R.string.athlete)
                        in 56..61 ->  getString(R.string.excellent)
                        in 62..65 ->  getString(R.string.great)
                        in 66..69 ->  getString(R.string.good)
                        in 70..73 ->  getString(R.string.average)
                        in 74..81 ->  getString(R.string.below_average)
                        else -> getString(R.string.poor)

                    }

            }else  if (usia in 26..35)
            {
                status= when(ibpm)
                {
                    in  49..54 -> getString(R.string.athlete)
                    in 55..61 ->  getString(R.string.excellent)
                    in 62..65 ->  getString(R.string.great)
                    in 66..70->  getString(R.string.good)
                    in 71..74 ->  getString(R.string.average)
                    in 75..81 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else  if (usia in 36..45)
            {
                status= when(ibpm)
                {
                    in  50..56 -> getString(R.string.athlete)
                    in 57..62 ->  getString(R.string.excellent)
                    in 63..66 ->  getString(R.string.great)
                    in 67..70->  getString(R.string.good)
                    in 71..75 ->  getString(R.string.average)
                    in 76..82 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else  if (usia in 46..55)
            {
                status= when(ibpm)
                {
                    in  50..57 -> getString(R.string.athlete)
                    in 58..63 ->  getString(R.string.excellent)
                    in 64..67 ->  getString(R.string.great)
                    in 68..71->  getString(R.string.good)
                    in 72..76 ->  getString(R.string.average)
                    in 77..83 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else  if (usia in 56..65)
            {

                status= when(ibpm)
                {
                    in  51..56 -> getString(R.string.athlete)
                    in 57..61 ->  getString(R.string.excellent)
                    in 62..67 ->  getString(R.string.great)
                    in 68..71->  getString(R.string.good)
                    in 72..75 ->  getString(R.string.average)
                    in 76..81 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else if (usia > 65)
            {

                status= when(ibpm)
                {
                    in  50..55 -> getString(R.string.athlete)
                    in 56..61 ->  getString(R.string.excellent)
                    in 62..65 ->  getString(R.string.great)
                    in 66..69->  getString(R.string.good)
                    in 70..73 ->  getString(R.string.average)
                    in 74..79 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }


        }else
        {

            if (usia in 18..25)
            {
                status= when(ibpm)
                {
                    in  54..60 -> getString(R.string.athlete)
                    in 61..65 ->  getString(R.string.excellent)
                    in 66..69 ->  getString(R.string.great)
                    in 70..73 ->  getString(R.string.good)
                    in 74..78 ->  getString(R.string.average)
                    in 79..84 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else  if (usia in 26..35)
            {

                status= when(ibpm)
                {
                    in 54..59 -> getString(R.string.athlete)
                    in 60..64 ->  getString(R.string.excellent)
                    in 65..68 ->  getString(R.string.great)
                    in 69..72 ->  getString(R.string.good)
                    in 73..76 ->  getString(R.string.average)
                    in 77..82 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else  if (usia in 36..45)
            {
                status= when(ibpm)
                {
                    in 54..59 -> getString(R.string.athlete)
                    in 60..64 ->  getString(R.string.excellent)
                    in 65..69 ->  getString(R.string.great)
                    in 70..73 ->  getString(R.string.good)
                    in 74..78 ->  getString(R.string.average)
                    in 79..84 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else  if (usia in 46..55)
            {
                status= when(ibpm)
                {
                    in 54..60 -> getString(R.string.athlete)
                    in 61..65 ->  getString(R.string.excellent)
                    in 66..69 ->  getString(R.string.great)
                    in 70..73 ->  getString(R.string.good)
                    in 74..77 ->  getString(R.string.average)
                    in 78..83 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else  if (usia in 56..65)
            {
                status= when(ibpm)
                {
                    in 54..59 -> getString(R.string.athlete)
                    in 60..64 ->  getString(R.string.excellent)
                    in 65..68 ->  getString(R.string.great)
                    in 69..73 ->  getString(R.string.good)
                    in 74..77 ->  getString(R.string.average)
                    in 78..83 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }

            }else if (usia > 65)
            {
                status= when(ibpm)
                {
                    in 54..59 -> getString(R.string.athlete)
                    in 60..64 ->  getString(R.string.excellent)
                    in 65..68 ->  getString(R.string.great)
                    in 69..72 ->  getString(R.string.good)
                    in 73..76 ->  getString(R.string.average)
                    in 77..84 ->  getString(R.string.below_average)
                    else -> getString(R.string.poor)

                }


            }

        }

        return status
    }


    fun showDialogResult(bpm:String, ibpm:Int)
    {
        val dialog = Dialog(this@HeartBeatActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        dialog.setContentView(R.layout.dialog_heartbeat_result)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val v = dialog.window!!.decorView
        v.setBackgroundResource(android.R.color.transparent)



        (dialog.findViewById<View>(R.id.img_close) as ImageView).setOnClickListener { dialog.dismiss() }

        (dialog.findViewById<View>(R.id.tvBPM) as TextView).setText(bpm + " BPM")
        val labelIndicator =  dialog.findViewById<View>(R.id.tvLabelIndicator) as TextView
        val labelDesc=  dialog.findViewById<View>(R.id.tvDesc) as TextView


        val btnSave=  dialog.findViewById<View>(R.id.btnSaveBPM) as ImageButton
        val btnShare=  dialog.findViewById<View>(R.id.btnShare) as ImageButton



        var status = getStatus(ibpm)
        labelIndicator.visibility = View.VISIBLE

        if (ibpm in 60..100)
        {
           // status = getString(R.string.healthy)
            labelIndicator.setBackgroundResource(R.drawable.green_rounded_corner)

        }else {
           // status = getString(R.string.sick)
            labelIndicator.setBackgroundResource(R.drawable.rounded_corner)
        }



        var desc = ""
        textToShare = "Detak Jantung Sebesar " + bpm + " BPM"
        textToShare += "Dinyatakan dalam keadaan  " + status


        if (ibpm in 0..39 )
        {
            desc = getString(R.string.red_desc_bpm)
            labelDesc.setBackgroundResource(R.drawable.shape_rect_round_dash_red)
            desc = desc + "\n" + "0 - 39"



        }else if (ibpm in 40..59)
        {
            desc =   getString(R.string.orange_desc_bpm)
            labelDesc.setBackgroundResource(R.drawable.shape_rect_round_dash_warning)
            desc = desc + "\n" + "40 - 59"

        }else if (ibpm in 60..100)
        {

            desc = getString(R.string.green_desc_bpm)
            labelDesc.setBackgroundResource(R.drawable.shape_rect_round_dash)
            desc = desc + "\n" + "60 - 100"
        }

        textToShare += desc

        labelDesc.setText(desc)

        labelIndicator.text = status


        btnSave.setOnClickListener(View.OnClickListener { saveHeartPulse(bpm) })

        btnShare.setOnClickListener(View.OnClickListener {

            if (textToShare.isNullOrBlank())
            {

                Toasty.warning(this@HeartBeatActivity, getString(R.string.no_data_to_share), Toast.LENGTH_SHORT, true)
                    .show()

            }else {
                val intentShare = Intent(Intent.ACTION_SEND)
                intentShare.setType("text/plain")
                intentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.heart_beat))
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


        dialog.show()
        dialog.window!!.attributes = lp



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
                        .setTitleText(getString(R.string.success))
                        .setContentText(String.format(Locale.getDefault(),"%s %s", "Data BPM ",getString(R.string.success_save) ))
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
