package com.kiowa.bluetoothlocatability.triangulationMethods

class Centroid(b : HashMap<String,Pair<FloatArray, Double>>) {

    private var beacons = b




    // method found https://www.sciencedirect.com/science/article/pii/S1319157816300842

    /**
     * findCurrentPointF estimates the loaction using the centroid of each triangle in the centroid created by the beacons
     * each centroid is weighted based off distance to the beacons
     */
    fun findCurrentPointF() : FloatArray{
        //TODO find the centroid based on the distances
        // FORMULA FOUND HERE : https://math.stackexchange.com/questions/90463/how-can-i-calculate-the-centroid-of-polygon
        var topX = 0.0f
        var bottomX = 0.0f
        var topY = 0.0f
        var bottomY = 0.0f


        for((_,v) in beacons){
            topX+=(v.first[0] * v.second.toFloat())
            topY+=(v.first[1] * v.second.toFloat())
            bottomX += v.second.toFloat()
            bottomY+= v.second.toFloat()
        }
        return floatArrayOf(topX/bottomX,topY/bottomY)
    }



}



