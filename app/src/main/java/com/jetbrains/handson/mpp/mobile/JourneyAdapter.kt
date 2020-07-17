package com.jetbrains.handson.mpp.mobile

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.journeys_list_layout.view.*

class JourneyAdapter(private var journeyList: List<Journey>) : RecyclerView.Adapter<JourneyAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.journeys_list_layout, parent, false)
        return MyViewHolder(view)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(journeyList[position].inbound, journeyList[position].outbound, journeyList[position].depTime, journeyList[position].arrTime, journeyList[position].duration, journeyList[position].date) { journeyList[position].button }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return journeyList.size
    }

    //the class is holding the list view
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(
            inbound: String,
            outbound: String,
            depTime: String,
            arrTime: String,
            duration: String,
            date: String,
            function: () -> String
        ) {
            itemView.departureTime.text = depTime.substring(11,16)
            itemView.arrivalTime.text = arrTime.substring(11,16)
            itemView.duration.text = duration
            itemView.date.text = date
            itemView.go_to_buy_button.setOnClickListener {
                ApplicationPresenter().onBuyButton(outbound, inbound, depTime.substring(5, 7).toInt(), depTime.substring(8, 10).toInt(), depTime.substring(11, 13).toInt(), depTime.substring(14, 16).toInt(), true)
            }
        }
    }

    fun updateData(data: List<Journey>) {
        journeyList = data
        notifyDataSetChanged()
    }
}