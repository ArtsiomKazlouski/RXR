package com.ank.rxr

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_bluetooth_select.*

class BluetoothSelectActivity: AppCompatActivity() {

    val BLE_PREF_NAME = "Ble"
    val BLE_DEVICE_ID = "DeviceId"

    private lateinit var blePref: SharedPreferences
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_select)
        blePref = applicationContext.getSharedPreferences(BLE_PREF_NAME, Context.MODE_PRIVATE)


        var dataset = arrayOf("s", "x", "stupid", "kotlin", "and", "i")

        viewManager = LinearLayoutManager(this)
        viewAdapter = BluetoothListAdabter(dataset)

        findViewById<RecyclerView>(R.id.rv).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        blePref.getString(BLE_DEVICE_ID, "")
    }

}

class BluetoothListAdabter(private val dataset: Array<String>) :
    RecyclerView.Adapter<BluetoothListAdabter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BluetoothListAdabter.MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bluetooth_view, parent, false) as TextView
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.text = dataset[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size
}