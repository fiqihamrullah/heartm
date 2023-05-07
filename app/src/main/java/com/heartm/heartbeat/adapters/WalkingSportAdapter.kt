package com.heartm.heartbeat.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heartm.heartbeat.MyApplication
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
        var tvResult:TextView

         var lyt_parent: View

        init {
            tvDayMonth = v.findViewById<View>(R.id.tvDayAndMonth) as TextView
            tvYear = v.findViewById<View>(R.id.tvYear) as TextView
            tvStepValue = v.findViewById<View>(R.id.tvStepValue) as TextView
            tvResult = v.findViewById<View>(R.id.tvResult) as TextView

             lyt_parent = v.findViewById<View>(R.id.lyt_parent) as View
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

        val daymonth = mydate.convertfromLongDate(b.created_at, "HH:ss")
        val year = mydate.convertfromLongDate(b.created_at, "dd MMM yyyy")
        holder.tvDayMonth.setText(daymonth?.trim())
        holder.tvYear.setText( year)



        holder.tvStepValue.setText( b.total_per_hari.toString().plus(" / ").plus(b.jumlah_target.toString()))

        if (b.total_per_hari < b.jumlah_target)
        {
            holder.tvResult.setText( MyApplication.Companion.instance?.resources?.getString(R.string.target_missed))
            holder.tvResult.setTextColor(Color.RED)

        }else {
            holder.tvResult.setText( MyApplication.Companion.instance?.resources?.getString(R.string.target_achieved))
            holder.tvResult.setTextColor(Color.GREEN)
        }



        holder.lyt_parent.setOnClickListener { view ->
            if (mOnItemClickListener != null)
            {
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
