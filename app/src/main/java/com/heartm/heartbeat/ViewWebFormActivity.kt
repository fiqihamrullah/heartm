package com.heartm.heartbeat

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar

class ViewWebFormActivity : AppCompatActivity()
{

    private lateinit var progressBar : ProgressBar
    private var url : String? = null

    private val URL_FORGOT_PASSWORD =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_base_url) + "resetpassword"

    private val URL_REGISTER =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_base_url) + "register"

    private val URL_UPDATE_PROFILE =
        MyApplication.Companion.instance?.resources?.getString(R.string.online_base_url) + "profil/" + UserAccount.getID()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_web_form)

        val pil = intent.extras!!.getInt("pil")

        if (pil==1)
        {
           initToolbar(getString(R.string.forgot_password),"")
            initComponentWebview(URL_FORGOT_PASSWORD)
        }else if (pil==2)
        {

            initToolbar(getString(R.string.sign_up),"")
            initComponentWebview(URL_REGISTER)
        }else if (pil==3)
        {

            initToolbar(getString(R.string.update_profile),"")
            initComponentWebview(URL_UPDATE_PROFILE)
        }


    }

    fun initComponentWebview(url:String)
    {

        val webView = findViewById<WebView>(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        /*
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView,
                                                  url: String): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                /*createWebPrintJob(view)*/
                /*createWebPrintJob(view)*/
               // myWebView = null
                printPDF(webView)
            }
        }*/

        webView.webChromeClient = MyWebChromeClient(this)
        webView.webViewClient = object : WebViewClient()
        {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?)
            {
                progressBar?.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest?
            ): Boolean {
                webView?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView, url: String?)
            {
                progressBar?.visibility = View.GONE
                // printPDF(view)
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                progressBar?.visibility = View.GONE
                super.onReceivedError(view, request, error)
            }

        }


        webView.clearCache(true)
        webView.clearHistory()
        webView.settings?.javaScriptEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.invokeZoomPicker()

        //  var htmlDocument : String = "<html><body><h1>Ka Me Ha Me Ha</h1></body></html>"
        //  webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null)

        webView.loadUrl(url)



    }

    fun initToolbar(title:String,subTitle:String)
    {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(title)
        supportActionBar?.setSubtitle(subTitle)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item?.itemId==android.R.id.home)
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private class MyWebChromeClient : WebChromeClient {
        private var context: Context?= null

        constructor(context : Context?):super()
        {
            this.context = context

        }




    }



}
