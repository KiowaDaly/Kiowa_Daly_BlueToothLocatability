package com.kiowa.bluetoothlocatability.triangulationMethods

import com.kiowa.bluetoothlocatability.utilities.BeaconData
import com.kiowa.bluetoothlocatability.utilities.BeaconScreenPoint
import kotlin.math.pow
import kotlin.math.sqrt

class Pythagoras(private val beacons: ArrayList<BeaconData>) : Triangulation {


    override fun calculateLocation(): BeaconScreenPoint {
        val myList = beacons.sortedBy { it.distance }.takeLast(2)
        val one = myList[0]
        val two = myList[1]
        val u = findDistance(one.coordinates, two.coordinates)

        val x = one.distance.pow(2) - two.distance.pow(2) + u.pow(2)
        val y = sqrt(one.distance.pow(2) - x.pow(2))
        return BeaconScreenPoint(x, y)
    }


    private fun findDistance(a: BeaconScreenPoint, b: BeaconScreenPoint): Double {
        val lhs = (b.x - a.x).pow(2)
        val rhs = (b.y - a.y).pow(2)
        return sqrt(lhs + rhs)
    }
}