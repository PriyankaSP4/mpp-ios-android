package com.jetbrains.handson.mpp.mobile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetbrains.handson.mpp.mobile.api.JourneyOption
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.journeys_list_layout.*
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), ApplicationContract.View,

    AdapterView.OnItemSelectedListener {
    val journeysForRecyclerView = ArrayList<Journey>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val presenter = ApplicationPresenter()
        presenter.onViewTaken(this)

        val outboundSpinner: Spinner = findViewById(outbound_spinner_control.id)
        ArrayAdapter.createFromResource(
            this,
            R.array.stations_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            outboundSpinner.adapter = adapter
        }

        val inboundSpinner: Spinner = findViewById(inbound_spinner_control.id)
        ArrayAdapter.createFromResource(
            this,
            R.array.stations_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            inboundSpinner.adapter = adapter
        }


        val journeysRecyclerView = findViewById<RecyclerView>(R.id.journeys_recycler_view)

        journeysRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val adapter = JourneyAdapter(journeysForRecyclerView)

        journeysRecyclerView.adapter = adapter


        val button: Button = findViewById(station_button.id)
        button.setOnClickListener {
            val origin = outboundSpinner.selectedItem.toString()
            val destination = inboundSpinner.selectedItem.toString()
            val time = LocalDateTime.now().plusMinutes(5).toString()
            presenter.onButtonPressed(origin, destination, time)
            adapter.updateData(journeysForRecyclerView)
        }


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
    /*
    val ticketsButton: Button = findViewById(go_to_buy_button.id)
    val ticketsButtonPosition = ticketsButton.getAdapterPosition()
    button.setOnClickListener {
        val ticket: <Ticket> = ticketSiteData[ticketsButton.getPosition()]
        presenter.onBuyButton(ticket.inbound, ticket.outbound, ticket.month, ticket.day, ticket.hour, ticket.minute, ticket.returnBool)
    }
    */
    override fun onItemSelected(parent: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
        parent?.getItemAtPosition(position)

    }

    override fun setLabel(text: String) {
        findViewById<TextView>(R.id.main_text).text = text
    }

    override fun showData(journeys: List<JourneyOption>) {
        journeys.drop(journeys.size)
        for (item in journeys) {
            journeysForRecyclerView.add(
                Journey(item.originStation.crs,
                    item.destinationStation.crs,
                    item.departureTime,
                    item.arrivalTime,
                    item.journeyDurationInMinutes.toString() + " min", item.departureTime.substring(8,10)+"/"+item.departureTime.substring(5,7)+"/"+item.departureTime.substring(0,4)
                )
            )
        }
    }

    override fun showAlert(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun openWebpage(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }


}