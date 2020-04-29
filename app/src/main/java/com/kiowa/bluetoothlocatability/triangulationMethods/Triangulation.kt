package com.kiowa.bluetoothlocatability.triangulationMethods

import com.kiowa.bluetoothlocatability.utilities.BeaconScreenPoint

interface Triangulation {
    fun calculateLocation(): BeaconScreenPoint
}