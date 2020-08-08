package app.bsc.db.drawing.view.alarms

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import app.bsc.db.drawing.R
import app.bsc.db.drawing.model.Alarm
import java.util.*

class AlarmsRecyclerAdapter(
    myDataset: ArrayList<Alarm>,
    onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<AlarmsRecyclerAdapter.AlarmsViewHolder>() {

    private var currentDataset: ArrayList<Alarm> = myDataset

    private var itemClickListener: OnItemClickListener = onItemClickListener

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    class AlarmsViewHolder(itemView: View, private var onItemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var itemTitle: TextView = itemView.findViewById(R.id.item_title)
        var itemDailyStatus: TextView = itemView.findViewById(R.id.item_detail)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener.onClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmsViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false) as CardView

        return AlarmsViewHolder(
            cardView,
            itemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlarmsViewHolder, position: Int) {
        holder.itemTitle.text =
            "${currentDataset[position].hour} : ${currentDataset[position].minute}"

        if (currentDataset[position].daily == 1)
            holder.itemDailyStatus.text = "Daily"
        else
            holder.itemDailyStatus.text = "One Time"

        if (currentDataset[position].minute.toString().length == 1) {
            holder.itemTitle.text =
                currentDataset[position].hour.toString() + ":0" + currentDataset[position].minute.toString()
        } else {
            holder.itemTitle.text =
                currentDataset[position].hour.toString() + ":" + currentDataset[position].minute.toString()
        }
    }

    override fun getItemCount() = currentDataset.size

}