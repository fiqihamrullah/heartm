package com.heartm.heartbeat.models

class DrugReceipt(no:Int)
{
   var receipt_no :Int=no
   var listObat: ArrayList<DrugUsage> =  ArrayList<DrugUsage>()


    fun init()
    {

    }


    fun addObat(obat:DrugUsage)
    {
        listObat?.add(obat)
        println("Masuk Tambah " + listObat?.size)
    }

    fun getObat(position:Int):DrugUsage?
    {
        return listObat?.get(position)
    }

    fun getSize():Int?
    {
        return listObat?.size
    }



}