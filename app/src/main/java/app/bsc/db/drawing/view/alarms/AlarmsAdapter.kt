package app.bsc.db.drawing.view.alarms

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import app.bsc.db.drawing.R
import app.bsc.db.drawing.model.Alarm
import java.util.ArrayList

class AlarmsRecyclerAdapter(private val myDataset: ArrayList<Alarm>, onItemClickListener: OnItemClickListener) :
                RecyclerView.Adapter<AlarmsRecyclerAdapter.AlarmsViewHolder>() {

    var currentDataset: ArrayList<Alarm>

    var mOnItemClickListener: OnItemClickListener

    init {
        currentDataset = myDataset
        mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener{
        fun onClick(position: Int)
    }

    class AlarmsViewHolder(itemView: View, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var itemTitle: TextView
        var itemDailyStatus: TextView

        var onItemClickListener: OnItemClickListener

        init{
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDailyStatus = itemView.findViewById(R.id.item_detail)

            this.onItemClickListener = onItemClickListener
            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            onItemClickListener.onClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AlarmsViewHolder {
        val cardView= LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false) as CardView

        return AlarmsViewHolder(
            cardView,
            mOnItemClickListener
        )
    }

    override fun onBindViewHolder(holder: AlarmsViewHolder, position: Int) {
        holder.itemTitle.text = currentDataset[position].hour.toString()  + ":" + currentDataset[position].minute.toString()

        if(currentDataset[position].daily == 1)
            holder.itemDailyStatus.text = "Daily"
        else
            holder.itemDailyStatus.text = "One Time"

        if(currentDataset[position].minute.toString().length == 1){
            holder.itemTitle.text = currentDataset[position].hour.toString()  + ":0" + currentDataset[position].minute.toString()
        }else{
            holder.itemTitle.text = currentDataset[position].hour.toString()  + ":" + currentDataset[position].minute.toString()
        }
    }

    override fun getItemCount() = currentDataset.size

    fun setDataset(newDataset: ArrayList<Alarm>){
        currentDataset = newDataset
        Log.i("AlarmsAdapter", "Changed to new dataset. Current contents of currentDataset: ")
        Log.i("AlarmsAdapter", currentDataset[currentDataset.size-1].toString())
    }
}