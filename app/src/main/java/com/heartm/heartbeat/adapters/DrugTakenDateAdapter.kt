package com.heartm.heartbeat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heartm.heartbeat.R
import com.heartm.heartbeat.models.DrugTakenDate
import com.heartm.heartbeat.models.HeartBeatRecord
import com.heartm.heartbeat.util.MyDateConverter
import java.util.ArrayList

class DrugTakenDateAdapter(

    items: ArrayList<DrugTakenDate>
) :
    RecyclerView.Adapter<DrugTakenDateAdapter.ViewHolder>() {
    private var items: ArrayList<DrugTakenDate> = ArrayList<DrugTakenDate>()
    private var mOnItemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            obj: DrugTakenDate?,
            position: Int
        )
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvDate: TextView

        var tvValue: TextView


        var lyt_parent: View

        init {
            tvDate = v.findViewById<TextView>(R.id.tvDate) as TextView

            tvValue = v.findViewById<View>(R.id.tvValue) as TextView



             lyt_parent = v.findViewById<View>(R.id.lyt_parent) as View
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_drug_takendate, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val b: DrugTakenDate = items[position]

        val mydate = MyDateConverter()


        val date = mydate.convertfromShortDate(b.tgl_pengambilan_obat, "dd MMM yyyy")
        holder.tvDate.setText(date)






        holder.tvValue.setText(b.jumlah_obat.toString())



        holder.lyt_parent.setOnClickListener { view ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(view, b, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    init {

        this.items = items
    }
}