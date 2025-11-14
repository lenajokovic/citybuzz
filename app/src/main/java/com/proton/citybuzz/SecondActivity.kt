package com.proton.citybuzz

import android.os.Bundle
import android.os.PersistableBundle
import java.util.Calendar
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.proton.citybuzz.data.model.Event
import java.time.LocalDate
import java.time.LocalTime

class SecondActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_create_event)

        /*val createEventButton = findViewById<Button>(R.id.save_event_button)
        createEventButton.setOnClickListener {
            createEvent()
        }

        val cancelButton = findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            finish()
        }*/
    }

    /*fun createEvent(){
        val eventName = findViewById<EditText>(R.id.event_name)

        val startDay = findViewById<NumberPicker>(R.id.start_day)
        startDay.minValue = 1
        startDay.maxValue = 31
        val startMonth = findViewById<NumberPicker>(R.id.start_month)
        startMonth.minValue = 1
        startMonth.maxValue = 12
        val startYear = findViewById<NumberPicker>(R.id.start_year)
        startYear.minValue = Calendar.getInstance().get(Calendar.YEAR)
        startYear.maxValue = startYear.minValue + 10

        val startHour = findViewById<NumberPicker>(R.id.start_hour)
        startHour.minValue = 0
        startHour.maxValue = 23
        val startMinute = findViewById<NumberPicker>(R.id.start_minute)
        startMinute.minValue = 0
        startMinute.maxValue = 59

        val duration = findViewById<EditText>(R.id.duration)
        val location = findViewById<EditText>(R.id.location)
        val description = findViewById<EditText>(R.id.description)

        val category = findViewById<Spinner>(R.id.category_picker)
        var selectedCategory = 0
        val adapter = ArrayAdapter.createFromResource(
            this,                    // Context
            R.array.publicity_levels,         // Array resource
            android.R.layout.simple_spinner_item  // Layout for spinner item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                selectedCategory = position
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        category.adapter = adapter

        CityBuzzApp.eventViewModel.addEvent(eventName.text.toString(),
            description.text.toString(),
            location.text.toString(),
            LocalDate.of(startYear.value, startMonth.value, startYear.value),
            LocalTime.of(startHour.value, startMinute.value),
            selectedCategory,
            CityBuzzApp.socialViewModel.loggedInUser.value?.id ?: 0
        )

        finish()
    }*/
}