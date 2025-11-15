package com.proton.citybuzz

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.proton.citybuzz.data.model.EventConverters
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar

class CreateEventActivity : AppCompatActivity() {

    private lateinit var startDay: NumberPicker
    private lateinit var startMonth: NumberPicker
    private lateinit var startYear: NumberPicker
    private lateinit var startHour: NumberPicker
    private lateinit var startMinute: NumberPicker
    var selectedCategory = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_event)
        setupUI()
        setupButtons()
    }

    fun setupUI() {
        startMonth = findViewById(R.id.start_month)
        startYear = findViewById(R.id.start_year)
        startDay = findViewById(R.id.start_day)
        startHour = findViewById(R.id.start_hour)
        startMinute = findViewById(R.id.start_minute)

        startDay.minValue = 1
        startDay.maxValue = 31
        startDay.value = LocalDate.now().dayOfMonth

        startMonth.minValue = 1
        startMonth.maxValue = 12
        startMonth.value = LocalDate.now().monthValue

        val currentYear = LocalDate.now().year
        startYear.minValue = currentYear
        startYear.maxValue = currentYear + 10
        startYear.value = currentYear

        startHour.minValue = 0
        startHour.maxValue = 23
        startMinute.minValue = 0
        startMinute.maxValue = 59


        val category = findViewById<Spinner>(R.id.category_picker)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.publicity_levels,         
            android.R.layout.simple_spinner_item
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
    }

    fun setupButtons() {
        val createEventButton = findViewById<Button>(R.id.save_event_button)
        createEventButton.setOnClickListener {
            createEvent()
        }

        val cancelButton = findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    fun createEvent() {
        val eventName = findViewById<EditText>(R.id.event_name)

        val location = findViewById<EditText>(R.id.location)
        val description = findViewById<EditText>(R.id.description)

        CityBuzzApp.getInstance().eventViewModel.addEvent(
            eventName.text.toString(),
            description.text.toString(),
            location.text.toString(),
            LocalDateTime.of(
                startYear.value,
                startMonth.value,
                startDay.value,
                startHour.value,
                startMinute.value
            ),
            privacy = selectedCategory,
            idUser = 420 // CityBuzzApp.getInstance().socialViewModel.loggedInUser.value!!.id,
        )

        CityBuzzApp.getInstance().eventViewModel.addEvent("Second",
            "Blahblah",
            "Beograd",
            LocalDateTime.of(2025, 10, 15, 12, 30),
            selectedCategory,
            CityBuzzApp.getInstance().socialViewModel.loggedInUser.value!!.id)

        Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show()
    }
}