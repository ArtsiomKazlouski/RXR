package com.ank.rxr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.ank.core.BtDevice
import com.ank.core.IBluetoothDeviceManager
import com.ank.rxr.bluetooth.BluetoothDeviceManager

class BluetoothSelectActivity : AppCompatActivity() {

    private lateinit var blePref: SharedPreferences
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var dataset: MutableList<BtDevice>

    companion object {
        const val BLE_PREF_NAME = "Ble"
        const val BLE_DEVICE_ID = "DeviceId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_select)
        blePref = applicationContext.getSharedPreferences(BLE_PREF_NAME, Context.MODE_PRIVATE)

        dataset = mutableListOf()

        var bm = BluetoothDeviceManager()

        viewManager = LinearLayoutManager(this)
        viewAdapter = BluetoothListAdabter(dataset) { btDev ->
            bm.saveCurrentDevise(btDev)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        viewAdapter.notifyDataSetChanged()


        bm.startListenForConnectedDevices { btDev ->
            dataset.add(btDev)
            viewAdapter.notifyDataSetChanged()
        }

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

class BluetoothListAdabter(private val dataset: MutableList<BtDevice>, var btSelected: (btDev: BtDevice) -> Unit) :
    RecyclerView.Adapter<BluetoothListAdabter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BluetoothListAdabter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bluetooth_view, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val btn = holder.view.findViewById<Button>(R.id.choose_button)
        val device = dataset[position]
        btn.text = device.name
        btn.setOnClickListener { v ->
            btSelected(device)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size
}