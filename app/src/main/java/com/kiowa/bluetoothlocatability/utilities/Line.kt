package com.kiowa.bluetoothlocatability.utilities

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Line(
    private val m: Double, private val b: Double,
    private val beacon1: BeaconScreenPoint,
    private val beacon2: BeaconScreenPoint
) {

    fun isOnLine(x: Double, y: Double): Boolean {
        return y == m * x + b
    }

    //https://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
    fun isOnApproximateLine(beacon0: BeaconScreenPoint): Boolean {
        val lhsTop = (beacon2.x - beacon1.x) * (beacon1.y - beacon0.y)
        val rhsTop = (beacon1.x - beacon0.x) * (beacon2.y - beacon1.y)
        val topOfEquation = abs(lhsTop - rhsTop)

        val lhsBottom = (beacon2.x - beacon1.x).pow(2)
        val rhsBotton = (beacon2.y - beacon1.y).pow(2)
        val bottomOfEquation = sqrt(lhsBottom + rhsBotton)

        val distance = topOfEquation / bottomOfEquation
        if (distance <= 6.0) {
            return true
        }
        return false
    }
}