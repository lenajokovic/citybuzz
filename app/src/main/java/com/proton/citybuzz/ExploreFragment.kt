package com.proton.citybuzz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment


class ExploreFragment: Fragment(R.layout.activity_explore) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateView()
    }

    fun populateView(){

        val eventListContainer = view?.findViewById<LinearLayout>(R.id.explore_event_container)
        val inflater = LayoutInflater.from(context!!)
        val eventList = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        setUpListView(eventList.findViewById<ListView>(R.id.list_view))

        eventListContainer?.addView(eventList)
    }

    fun setUpListView(listView: ListView){

        data class Item(val event_id: Int, val event_name: String, val profile_pic: Int, val user_name: String)

        val items = listOf(
            Item(1, "Apple", R.drawable.ic_launcher_background, "Mia"),
            Item(2, "Orange", R.drawable.ic_launcher_background, "Mia"),
            Item(3, "Banana", R.drawable.ic_launcher_background,  "Mia")
        )

        val adapter = object : ArrayAdapter<Item>(context!!, 0, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false)

                val item = getItem(position)

                val profile_pic = view.findViewById<ImageView>(R.id.profile_pic)
                val event_name = view.findViewById<TextView>(R.id.event_name)
                val user_name = view.findViewById<TextView>(R.id.user_name)

                profile_pic.setImageResource(item?.profile_pic ?: 0)
                event_name.text = item?.event_name
                user_name.text = item?.user_name

                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            showEventDetails(items[position].event_id)
        }
    }

    fun showEventDetails(event_id: Int){

    }

}