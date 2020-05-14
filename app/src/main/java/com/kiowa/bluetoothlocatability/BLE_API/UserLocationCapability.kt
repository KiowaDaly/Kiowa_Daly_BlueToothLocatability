package com.kiowa.bluetoothlocatability.BLE_API

import android.content.Context
import android.content.Intent
import com.kiowa.bluetoothlocatability.utilities.BeaconScreenPoint
import com.kiowa.bluetoothlocatability.utilities.Constants

class UserLocationCapability {
    companion object {
        fun startSystem(
            context: Context,
            beacons: HashMap<Int, BeaconScreenPoint>,
            beacon1: BeaconScreenPoint
        ) {
            val intent = Intent(".BLE_API.BluetoothLeService")
            intent.putExtra(Constants.BEACON_MAP, beacons)
            intent.putExtra(Constants.FIRST_BEACON, beacon1)
            //startService(intent)
        }
    }
}