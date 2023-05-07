package com.heartm.heartbeat.adapters




import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heartm.heartbeat.R
import com.heartm.heartbeat.models.DrugReceipt
import com.heartm.heartbeat.models.DrugUsage

import com.heartm.heartbeat.util.MyDateConverter
import kotlinx.android.synthetic.main.activity_dashboard_actvity.*
import java.util.*

class DrugReceiptAdapter(

    items: ArrayList<DrugReceipt>
) :
    RecyclerView.Adapter<DrugReceiptAdapter.ViewHolder>() {
    private var items: ArrayList<DrugReceipt> = ArrayList<DrugReceipt>()
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
        var tvAmount : TextView
        var tvStatusOfDrug : TextView

        // var lyt_parent: View

        init {
            tvDayMonth = v.findViewById<View>(R.id.tvDayAndMonth) as TextView
            tvYear = v.findViewById<View>(R.id.tvYear) as TextView
            tvDrugName = v.findViewById<View>(R.id.tvDrugName) as TextView
            tvStatusOfDrug = v.findViewById<View>(R.id.tvStatusOfDrug) as TextView
            tvAmount = v.findViewById<View>(R.id.tvAmount) as TextView

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
        val b: DrugReceipt = items[position]
        val mydate = MyDateConverter()

        println("Posisi " + position)

        var content  = ""

        for(index in 1..(b.getSize() ?:1))  //convert int? to int beside using !!
        {
            val obat = b.getObat(index-1)
            content = content.plus(obat?.obat)// + "<br/><i>Jumlah <b>" + obat?.jumlah.toString() + " Pcs </b> ")

            val arrsplit : List<String> = obat?.waktu_makan!!.split(",")
            content = content.plus(" Dosis <b>" + arrsplit.size.toString() + "x / Hr</b></i>")

            content = content.plus("<br/>- - - - - - - - - - - - -  <br/>")




        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tvDayMonth.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY))
        } else
            holder.tvDayMonth.setText(Html.fromHtml(content))

       // val daymonth = mydate.convertfromShortDate(b.tgl_pengambilan_obat, "dd MMM yyyy")

        if (b.getSize()!!>0)
        {
            val nextdateoftakendrug =
                mydate.convertfromShortDate(b.getObat(0)?.tgl_ambil_selanjutnya, "dd MMM yyyy")
            holder.tvYear.setText("Selanjutnya " + nextdateoftakendrug)
        }
        // holder.tvDayMonth.setText(daymonth)






        //  holder.tvDayMonth.setText(b.obat)




        holder.tvDrugName.setText("")
        holder.tvDrugName.visibility = View.GONE

        //  holder.tvAmount.text = b.jumlah.toString() + " Pcs"
        holder.tvAmount.visibility = View.GONE

    //    val arrsplit : List<String> = b.waktu_makan.split(",")
    //    content = content.plus(" Diminum <b>" + arrsplit.size.toString() + "x / Hari</b></i>")
        //  holder.tvStatusOfDrug.text = arrsplit.size.toString() + "x / Hari"
        holder.tvStatusOfDrug.visibility = View.GONE




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