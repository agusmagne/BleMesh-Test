package com.example.test_mesh.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.test_mesh.R
import com.example.test_mesh.model.datamodels.scanner.ExtendedBluetoothDevice
import com.example.test_mesh.model.datamodels.scanner.ScannerLiveData
import kotlinx.android.synthetic.main.single_device.view.*

class DevicesAdapter(
    owner: LifecycleOwner,
    scannerLiveData: ScannerLiveData,
    private val devices: MutableList<ExtendedBluetoothDevice> = scannerLiveData.getDevices(),
    private val onDeviceClickListeter: (ExtendedBluetoothDevice) -> Unit
) :
    RecyclerView.Adapter<DevicesViewHolder>() {

    init {
        scannerLiveData.observe(owner, { devices -> updateDevices(devices) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.single_device, parent, false)
        return DevicesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        val device = devices[position]
        holder.bindView(device)
        holder.itemView.setOnClickListener { onDeviceClickListeter(device) }
    }

    override fun getItemCount(): Int = devices.size

    private fun updateDevices(devices: ScannerLiveData) {
        val index = devices.getUpdatedDeviceIndex()
        if (index != null) {
            notifyItemChanged(index)
        } else {
            notifyDataSetChanged()
        }
    }
}

class DevicesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(device: ExtendedBluetoothDevice) {
        itemView.device_name.text = device.name
        itemView.device_address.text = device.device.address
        val rssiPercent = (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f)).toInt()
        itemView.rssi.setImageLevel(rssiPercent)
    }
}