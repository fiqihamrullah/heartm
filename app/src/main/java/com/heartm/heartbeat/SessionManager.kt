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
    private val DOCTOR_NAME = "DOKTOR_NAME"
    private val DOCTOR_PHONE = "DOCTOR_PHONE"
    private val AGE = "AGE"
    private val GENDER = "GENDER"

    private val STEP_TARGET = "STEP_TARGET"
    private val TOTAL_STEP = "TOTAL_STEP"
    private val NEXT_DATE_DRUG_TAKEN = "NEXT_DATE_DRUG_TAKEN"
    private val TIME_DRUG_USAGE = "TIME_DRUG_USAGE"

    private val LAST_STEP_SAVED = "LAST_STEP_SAVED"



    constructor(context: Context)
    {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME,MODE)
        editor = pref.edit()

    }

    fun saveStepTarget(step:Int)
    {
        editor.putInt(STEP_TARGET,step)
        editor.commit()
    }

    fun getStepTarget():Int
    {
        return pref.getInt(STEP_TARGET,0)
    }

    fun saveNewStep(new_step:Int)
    {
        editor.putInt(TOTAL_STEP,new_step)
        editor.commit()
    }

    fun saveStep(new_step:Int)
    {
        val step = pref.getInt(TOTAL_STEP,0) + new_step
        editor.putInt(TOTAL_STEP,step)
        editor.commit()
    }

    fun getTotalStep():Int
    {
        return pref.getInt(TOTAL_STEP,0)
    }

    fun saveNextDateDrugTaken(date:String)
    {
        editor.putString(NEXT_DATE_DRUG_TAKEN,date)
        editor.commit()
    }

    fun getNextDateDrugTaken():String
    {
        return pref.getString(NEXT_DATE_DRUG_TAKEN,"").orEmpty()
    }


    fun saveLastStepSaved(date:String)
    {
        editor.putString(LAST_STEP_SAVED,date)
        editor.commit()
    }

    fun getLastStepSaved():String
    {
        return pref.getString(LAST_STEP_SAVED,"").orEmpty()
    }

    fun saveTimeofDrugUsage(time:String)
    {
        editor.putString(TIME_DRUG_USAGE,time)
        editor.commit()
    }

    fun getTimeofDrugUsage():String
    {
        return pref.getString(TIME_DRUG_USAGE,"").orEmpty()
    }


    fun createLoginSession(
        ID: String,
        username: String,
        email: String,
        dokter_name: String,
        dokter_phone : String,
        age : String,
        gender: String,
        api_token: String,
        expired_date: String,
        access : Int
    ) {

        editor.putBoolean(IS_LOGIN, true)
        editor.putString(USER_ID, ID)

        editor.putString(USERNAME, username)
        editor.putString(EMAIL, email)

        editor.putString(DOCTOR_NAME, dokter_name)
        editor.putString(DOCTOR_PHONE, dokter_phone)

        editor.putString(AGE, age)
        editor.putString(GENDER, gender)


        editor.putString(API_TOKEN,api_token)
        editor.putString(TOKEN_EXPIRED_DATE, expired_date)


        editor.putInt(ACCESS,access)


        editor.commit()
    }

    fun createUserProfiles()
    {
        UserAccount.setID(pref.getString(USER_ID, null).toString())
        UserAccount.setEmail(pref.getString(EMAIL, null).toString())
        UserAccount.setUserName(pref.getString(USERNAME, null).toString())
        UserAccount.setDoctorName(pref.getString(DOCTOR_NAME, null).toString())
        UserAccount.setDoctorPhoneNumber(pref.getString(DOCTOR_PHONE, null).toString())
        UserAccount.setUsia(pref.getString(AGE, "")!!.toInt())
        UserAccount.setGender(pref.getString(GENDER, null).toString())

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