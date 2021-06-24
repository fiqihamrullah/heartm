package com.heartm.heartbeat.adapters




import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heartm.heartbeat.R
import com.heartm.heartbeat.models.HeartBeatRecord

import com.heartm.heartbeat.util.MyDateConverter
import java.util.*

class HeartBeatAdapter(

    items: ArrayList<HeartBeatRecord>
) :
    RecyclerView.Adapter<HeartBeatAdapter.ViewHolder>() {
    private var items: ArrayList<HeartBeatRecord> = ArrayList<HeartBeatRecord>()
    private var mOnItemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            obj: HeartBeatRecord?,
            position: Int
        )
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvDayMonth: TextView
        var tvYear: TextView
        var tvValue: TextView
        var tvStatus: TextView

        // var lyt_parent: View

        init {
            tvDayMonth = v.findViewById<View>(R.id.tvDayAndMonth) as TextView
            tvYear = v.findViewById<View>(R.id.tvYear) as TextView
            tvValue = v.findViewById<View>(R.id.tvValue) as TextView
            tvStatus = v.findViewById<View>(R.id.tvStatus) as TextView


            // lyt_parent = v.findViewById(R.id.lyt_parent) as View
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_sport_step, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val b: HeartBeatRecord = items[position]

        val mydate = MyDateConverter()

        val daymonth = mydate.convertfromLongDate(b.created_at, "dd MM")
        val year = mydate.convertfromLongDate(b.created_at, "yyyy")
        holder.tvDayMonth.setText(daymonth)
        holder.tvYear.setText("Tahun " + year)

        var status : String=""

        holder.tvStatus.setText(status)



        holder.tvValue.setText(b.bpm)


        /*
        holder.lyt_parent.setOnClickListener { view ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(view, b, position)
            }
        }*/

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
