package com.jetbrains.handson.mpp.mobile

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jetbrains.handson.mpp.mobile.api.DateTime
import kotlinx.android.synthetic.main.journeys_list_layout.view.*

class JourneyAdapter(private var journeyList: List<Journey>, val presenter: ApplicationPresenter) : RecyclerView.Adapter<JourneyAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.journeys_list_layout, parent, false)
        return MyViewHolder(view, presenter)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(journeyList[position].originStation, journeyList[position].destinationStation,
            journeyList[position].depTime, journeyList[position].arrTime,
            journeyList[position].duration, journeyList[position].date, journeyList[position].button)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return journeyList.size
    }

    //the class is holding the list view
    class MyViewHolder(itemView: View, val presenter: ApplicationPresenter) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(
            originStation: String,
            destinationStation: String,
            depTime: DateTime,
            arrTime: DateTime,
            duration: String,
            date: DateTime, go_to_buy_button: String
        ){
            itemView.departureTime.text = depTime.dateTime.format("HH:mm")
            itemView.arrivalTime.text = arrTime.dateTime.format("HH:mm")
            itemView.duration.text = duration
            itemView.date.text = date.dateTime.format("dd:MM:yyyy")
            itemView.go_to_buy_button.setOnClickListener {
                presenter.onBuyButton(originStation, destinationStation, date.dateTime.month1, date.dateTime.dayOfMonth, depTime.dateTime.format("HH"), depTime.dateTime.format("mm"), true)
            }
        }
    }



    fun updateData(data: List<Journey>) {
        journeyList = data
        notifyDataSetChanged()
    }
}