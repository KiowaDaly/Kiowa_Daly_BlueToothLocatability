package com.kiowa.bluetoothlocatability.triangulationMethods

import android.util.Log
import com.kiowa.bluetoothlocatability.utilities.BeaconData
import com.kiowa.bluetoothlocatability.utilities.BeaconScreenPoint
import com.kiowa.bluetoothlocatability.utilities.Constants
import kotlin.math.pow


/**
 * https://www.101computing.net/cell-phone-trilateration-algorithm/
 *
 */
class CellTowerTrilateration(private val beacons: ArrayList<BeaconData>) : Triangulation {



    /**
     * Method Trilateration calculates the estimated coordinate of the user using the coordinates of
     * the three nearest beacons.
     * Each of the 3 beacons have a circle surrounding them and the radius = distance from current position
     * Current location = intersection point of the 3 triangles
     */
    override fun calculateLocation(): BeaconScreenPoint {
        //region debug messages1 TODO REMOVE
        Log.i(Constants.TRILATERATION, "all beacons + distance ->\n")
        for (b in beacons) {

            Log.i(
                Constants.TRILATERATION,
                "Beacon" + b.beaconID + ": (" + b.coordinates.x + "," + b.coordinates.y + ") and distance =" + b.distance
            )
        }
        //endregion

        //get closest 3, since they will have the most influence an relevancy of the location
        val closest3 =
            beacons.sortedByDescending { it.distance }.takeLast(3)
        //region debug2 TODO REMOVE
        Log.i(Constants.TRILATERATION, "Closest 3 ->")
        for (b in closest3) {
            Log.i(
                Constants.TRILATERATION,
                "Beacon" + b.beaconID + ": (" + b.coordinates.x + "," + b.coordinates.y + ") and distance =" + b.distance
            )
        }
        //endregion
        //setting up the helper variables
        val x1 = closest3[0].coordinates.x
        val y1 = closest3[0].coordinates.y
        val r1 = closest3[0].distance

        val x2 = closest3[1].coordinates.x
        val y2 = closest3[1].coordinates.y
        val r2 = closest3[1].distance

        val x3 = closest3[2].coordinates.x
        val y3 = closest3[2].coordinates.y
        val r3 = closest3[2].distance

        //Calculate the current position using distance as radius
        val a = (2*x2) -(2*x1)
        val b = (2*y2) - (2*y1)
        val c = (r1.pow(2) - r2.pow(2) - x1.pow(2) + x2.pow(2) - y1.pow(2) +y2.pow(2))
        val d = (2*x3) - (2*x2)
        val e = (2*y3) - (2*y2)
        val f = (r2.pow(2) - r3.pow(2) - x2.pow(2) + x3.pow(2) - y2.pow(2) +y3.pow(2))
        val x = ((c*e) - (f*b)) / ((e*a) - (b*d))
        val y = ((c*d) - (a*f)) / ((b*d) - (a*e))
        return BeaconScreenPoint(x, y)
    }
}