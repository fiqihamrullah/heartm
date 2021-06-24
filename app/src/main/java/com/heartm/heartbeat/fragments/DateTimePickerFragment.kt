package com.heartm.heartbeat.fragments


import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.heartm.heartbeat.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DateTimePickerFragment : DialogFragment()
{
    private var edHolder: TextView? = null

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, day ->
            val strdate = view.dayOfMonth.toString() + "/" + (view.month + 1) + "/" + view.year
            edHolder!!.text = strdate


        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity!!, dateSetListener, year, month, day)
    }

    fun setHolder(edHolder: TextView) {
        this.edHolder = edHolder
    }
}
