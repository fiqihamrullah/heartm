package com.heartm.heartbeat

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MyApplication : Application()
{

    private var requestQueue: RequestQueue? = null
    override fun onCreate()
    {
        super.onCreate()
        instance = this
    }

    fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(applicationContext)
        return requestQueue
    }


    fun addToRequestQueue(request: Request<*>, tag: String) {
        request.tag = tag
        getRequestQueue()?.add(request)
    }

    fun cancelAllRequests(tag: String)
    {
        getRequestQueue()?.cancelAll(tag)
    }

    companion object
    {
        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }

}