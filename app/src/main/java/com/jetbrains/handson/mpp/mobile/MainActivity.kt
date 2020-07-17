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
import java.time.LocalDateTime
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ApplicationContract.View,

    AdapterView.OnItemSelectedListener {
    val journeysForRecyclerView = ArrayList<Journey>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val presenter = ApplicationPresenter()
        presenter.onViewTaken(this)


        val outboundAutocomplete: AutoCompleteTextView = findViewById(outbound_autocomplete_control.id)
        ArrayAdapter(this, android.R.layout.simple_spinner_item, presenter.stations)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                outboundAutocomplete.setAdapter(adapter)
            }

        val inboundAutocomplete: AutoCompleteTextView = findViewById(inbound_autocomplete_control.id)
        ArrayAdapter(this, android.R.layout.simple_spinner_item, presenter.stations)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                inboundAutocomplete.setAdapter(adapter)
            }

        val journeysRecyclerView = findViewById<RecyclerView>(R.id.journeys_recycler_view)

        journeysRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val adapter = JourneyAdapter(journeysForRecyclerView, presenter)

        journeysRecyclerView.adapter = adapter


        val button: Button = findViewById(station_button.id)
        button.setOnClickListener {
            val origin = outboundAutocomplete.text.toString()
            val destination = inboundAutocomplete.text.toString()
            val time = LocalDateTime.now().plusMinutes(5).toString()
            presenter.onButtonPressed(origin, destination, time)
            adapter.updateData(journeysForRecyclerView)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
        parent?.getItemAtPosition(position)

    }

    override fun setLabel(text: String) {
        findViewById<TextView>(R.id.main_text).text = text
    }

    override fun showData(journeys: List<JourneyOption>) {
        journeysForRecyclerView.clear()
        for (item in journeys) {
            journeysForRecyclerView.add(
                Journey(
                    item.originStation.crs,
                    item.destinationStation.crs,
                    item.departureTime,
                    item.arrivalTime,
                    item.journeyDurationInMinutes.toString() + " min", item.departureTime
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
