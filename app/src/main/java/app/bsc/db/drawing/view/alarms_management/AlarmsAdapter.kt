package app.bsc.db.drawing.view.alarms_management

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

class AlarmsRecyclerAdapter(private val myDataset: ArrayList<Alarm>) :
    RecyclerView.Adapter<AlarmsRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemTitle: TextView

        init{
            itemTitle = itemView.findViewById(R.id.item_title)
            itemView.setOnClickListener {
                Log.i("myadapter", "setting card view onclick")
            }
        }
    }

    interface onItemClickListener{
        fun onClick(position: Int){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        val cardView= LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false) as CardView

        val vh = MyViewHolder(cardView)

        vh.itemView.setOnClickListener {
            Log.i("isfhaisdd", "FINALLY WORKING!!!")
        }

        return vh
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemTitle.text = myDataset[position].hour.toString()  + ":" + myDataset[position].minute.toString()




        if(myDataset[position].minute.toString().length == 1){
            holder.itemTitle.text = myDataset[position].hour.toString()  + ":0" + myDataset[position].minute.toString()
        }else{
            holder.itemTitle.text = myDataset[position].hour.toString()  + ":" + myDataset[position].minute.toString()
        }


    }

    override fun getItemCount() = myDataset.size

}