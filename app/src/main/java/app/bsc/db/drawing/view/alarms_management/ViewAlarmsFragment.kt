package app.bsc.db.drawing.view.alarms_management

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bsc.db.drawing.R
import app.bsc.db.drawing.model.Alarm
import app.bsc.db.drawing.util.db.DBHelper
import java.util.ArrayList

class ViewAlarmsFragment : Fragment() {
    companion object{
        var listView : RecyclerView? = null

        var alarmsList = ArrayList<Alarm>()

        fun addAlarm(alarm: Alarm){
            alarmsList.add(alarm)
        }

        var db: DBHelper? = null

        fun refreshData() {
            Log.i("ViewAlarmsFragment", "refreshData() was called")
            alarmsList = db!!.allAlarms
            val adapter = MyAdapter(alarmsList)
            listView!!.adapter = adapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listViewAlarms)
        listView!!.apply {
            layoutManager = LinearLayoutManager(this@ViewAlarmsFragment.context)
            adapter = MyAdapter(alarmsList)
        }
        db = DBHelper(context!!)
        refreshData()
    }

    class MyAdapter(private val myDataset: ArrayList<Alarm>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var itemTitle: TextView

            init{
                itemTitle = itemView.findViewById(R.id.item_title)

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.MyViewHolder {
            val cardView= LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false) as CardView

            return MyViewHolder(cardView)
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
}