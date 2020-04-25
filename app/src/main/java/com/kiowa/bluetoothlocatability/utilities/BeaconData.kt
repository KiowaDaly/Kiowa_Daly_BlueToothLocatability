package com.kiowa.bluetoothlocatability.utilities

import java.io.Serializable

data class BeaconData(val beaconID: Int, val coordinates: BeaconScreenPoint, val distance: Double) :
    Serializable