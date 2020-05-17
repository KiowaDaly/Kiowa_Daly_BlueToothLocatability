package com.kiowa.bluetoothlocatability.bleApi

import android.content.Context
import android.content.Intent
import com.kiowa.bluetoothlocatability.utilities.BeaconScreenPoint
import com.kiowa.bluetoothlocatability.utilities.Constants

class UserLocationCapability {
    companion object {
        //Companion object that is similar to java static methods
        fun startSystem(
            context: Context,
            beacons: HashMap<Int, BeaconScreenPoint>,
            beacon1: BeaconScreenPoint,
            deviceNameFilters: ArrayList<String>
        ) {
            val intent = Intent(".BLE_API.BluetoothLeService")
            intent.putExtra(Constants.BEACON_MAP, beacons)
            intent.putExtra(Constants.FIRST_BEACON, beacon1)
            intent.putExtra(Constants.DEVICE_NAMES, deviceNameFilters)
            context.startService(intent)
        }
    }


}