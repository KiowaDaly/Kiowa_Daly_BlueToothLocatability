package com.kiowa.bluetoothlocatability.triangulationMethods

import com.kiowa.bluetoothlocatability.utilities.BeaconData
import com.kiowa.bluetoothlocatability.utilities.BeaconScreenPoint

class Centroid(private val beacons: ArrayList<BeaconData>) {






    // method found https://www.sciencedirect.com/science/article/pii/S1319157816300842

    /**
     * findCurrentPointF estimates the loaction using the centroid of each triangle in the centroid created by the beacons
     * each centroid is weighted based off distance to the beacons
     */
    fun findCurrentPointF(): BeaconScreenPoint {
        //TODO find the centroid based on the distances
        // FORMULA FOUND HERE : https://math.stackexchange.com/questions/90463/how-can-i-calculate-the-centroid-of-polygon
        var topX = 0.0
        var bottom = 0.0
        var topY = 0.0



        for (b in beacons) {
            topX += (b.coordinates.x * b.distance)
            topY += (b.coordinates.y * b.distance)
            bottom += b.distance

        }
        return BeaconScreenPoint(topX / bottom, topY / bottom)
    }



}



