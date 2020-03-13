package com.kiowa.bluetoothlocatability.triangulationMethods

import kotlin.math.pow


/**
 * https://www.101computing.net/cell-phone-trilateration-algorithm/
 *
 */
class CellTowerTrilateration(beacons : HashMap<String,Pair<FloatArray, Double>>){
    private var listedBeacons = beacons.toList()


    /**
     * Method Trilateration calculates the estimated coordinate of the user using the coordinates of
     * the three nearest beacons.
     * Each of the 3 beacons have a circle surrounding them and the radius = distance from current position
     * Current location = intersection point of the 3 triangles
     */
    fun trilaterate()  : FloatArray{
        //get closest 3
        val clostest3 =
            listedBeacons.sortedByDescending { it.second.second }.takeLast(3)


        val x1= clostest3[0].second.first[0]
        val y1= clostest3[0].second.first[1]
        val r1= clostest3[0].second.second.toFloat()

        val x2= clostest3[1].second.first[0]
        val y2= clostest3[1].second.first[1]
        val r2= clostest3[1].second.second.toFloat()

        val x3= clostest3[2].second.first[0]
        val y3= clostest3[2].second.first[1]
        val r3= clostest3[2].second.second.toFloat()

        //Calculate the current position using distance as radius
        val a = 2*x2 -2*x1
        val b = 2*y2 - 2*y1
        val c = (r1.pow(2) - r2.pow(2) - x1.pow(2) + x2.pow(2) - y1.pow(2) +y2.pow(2)).toFloat()
        val d = 2*x3 - 2*x2
        val e = 2*y3 - 2*y2
        val f = (r2.pow(2) - r3.pow(2) - x2.pow(2) + x3.pow(2) - y2.pow(2) +y3.pow(2)).toFloat()
        val x = (c*e - f*b) / (e*a - b*d)
        val y = (c*d - a*f) / (b*d - a*e)
        return floatArrayOf(x,y)
    }
}