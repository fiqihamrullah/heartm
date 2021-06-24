package com.heartm.heartbeat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class SessionManager {
    private var  pref : SharedPreferences
    private  var editor : SharedPreferences.Editor

    private var context: Context

    private val MODE = 0 //Private Mode

    private val PREF_NAME  = "HEART_M"
    private val IS_LOGIN = "IS_LOGIN"


    private val API_TOKEN = "API_TOKEN"
    private val TOKEN_EXPIRED_DATE = "TOKEN_EXPIRED_DATE"

    private val USERNAME = "USERNAME"
    private val EMAIL = "EMAIL"
    private val USER_ID = "PASIEN_ID"
    private val ACCESS = "ACCESS"


    constructor(context: Context)
    {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME,MODE)
        editor = pref.edit()

    }

    fun createLoginSession(
        ID: String,
        username: String,
        email: String,
        api_token: String,
        expired_date: String,
        access : Int
    ) {

        editor.putBoolean(IS_LOGIN, true)
        editor.putString(USER_ID, ID)

        editor.putString(USERNAME, username)
        editor.putString(EMAIL, email)

        editor.putString(API_TOKEN,api_token)
        editor.putString(TOKEN_EXPIRED_DATE, expired_date)


        editor.putInt(ACCESS,access)


        editor.commit()
    }

    fun createUserProfiles() {
        UserAccount.setID(pref.getString(USER_ID, null).toString())
        UserAccount.setEmail(pref.getString(EMAIL, null).toString())
        UserAccount.setUserName(pref.getString(USERNAME, null).toString())
        UserAccount.setToken(pref.getString(API_TOKEN, null).toString())
        UserAccount.setAccess(pref.getInt(ACCESS,0))



    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }


    fun logout() {
        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()


        val i = Intent(context, SplashActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)


    }

}