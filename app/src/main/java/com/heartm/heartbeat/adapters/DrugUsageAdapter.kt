package com.heartm.heartbeat.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heartm.heartbeat.R
import com.heartm.heartbeat.models.DrugUsage

import com.heartm.heartbeat.util.MyDateConverter
import kotlinx.android.synthetic.main.activity_dashboard_actvity.*
import java.util.*

class DrugUsageAdapter(

    items: ArrayList<DrugUsage>
) :
    RecyclerView.Adapter<DrugUsageAdapter.ViewHolder>() {
    private var items: ArrayList<DrugUsage> = ArrayList<DrugUsage>()
    private var mOnItemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(
            view: View?,
            obj: DrugUsage?,
            position: Int
        )
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvDayMonth: TextView
        var tvYear: TextView
        var tvDrugName: TextView
        var tvStatusOfDrug : TextView

        // var lyt_parent: View

        init {
            tvDayMonth = v.findViewById<View>(R.id.tvDayAndMonth) as TextView
            tvYear = v.findViewById<View>(R.id.tvYear) as TextView
            tvDrugName = v.findViewById<View>(R.id.tvDrugName) as TextView
            tvStatusOfDrug = v.findViewById<View>(R.id.tvStatusOfDrug) as TextView

            // lyt_parent = v.findViewById(R.id.lyt_parent) as View
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_drug_usage, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val b: DrugUsage = items[position]

        val mydate = MyDateConverter()

        val daymonth = mydate.convertfromShortDate(b.tgl_pengambilan_obat, "dd MMM yyyy")
        val nextdateoftakendrug = mydate.convertfromShortDate(b.tgl_ambil_selanjutnya, "dd MMM yyyy")
        holder.tvDayMonth.setText(daymonth)
        holder.tvYear.setText("Selanjutnya " + nextdateoftakendrug)



        holder.tvDrugName.setText(b.obat)

        val arrsplit : List<String> = b.waktu_makan.split(",")
        holder.tvStatusOfDrug.text = arrsplit.size.toString() + "x / Hari"


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