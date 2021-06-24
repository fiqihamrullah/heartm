package com.heartm.heartbeat.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heartm.heartbeat.R
import com.heartm.heartbeat.models.WalkingSport
import com.heartm.heartbeat.util.MyDateConverter
import java.util.*

class WalkingSportAdapter(

    items: ArrayList<WalkingSport>
) :
    RecyclerView.Adapter<WalkingSportAdapter.ViewHolder>() {
    private var items: ArrayList<WalkingSport> = ArrayList<WalkingSport>()
    private var mOnItemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            obj: WalkingSport?,
            position: Int
        )
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvDayMonth: TextView
        var tvYear: TextView
        var tvStepValue: TextView

        // var lyt_parent: View

        init {
            tvDayMonth = v.findViewById<View>(R.id.tvDayAndMonth) as TextView
            tvYear = v.findViewById<View>(R.id.tvYear) as TextView
            tvStepValue = v.findViewById<View>(R.id.tvStepValue) as TextView

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
        val b: WalkingSport = items[position]

        val mydate = MyDateConverter()

        val daymonth = mydate.convertfromLongDate(b.created_at, "dd MMM")
        val year = mydate.convertfromLongDate(b.created_at, "yyyy")
        holder.tvDayMonth.setText(daymonth)
        holder.tvYear.setText("Tahun " + year)



        holder.tvStepValue.setText( b.jumlah_langkah.toString())


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
