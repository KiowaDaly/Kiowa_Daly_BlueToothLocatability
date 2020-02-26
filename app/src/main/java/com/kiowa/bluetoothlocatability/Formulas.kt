package com.kiowa.bluetoothlocatability

class Formulas {
    fun rssiDistanceFormula(rssi: Double, P: Double): Double {
        //d = 10 ^ ((P-Rssi) / 10n) (n ranges from 2 to 4)
        val n = 2
        val bottom = (10 * n).toDouble()
        val top = P - rssi
        return Math.pow(10.0, top / bottom)
    }
}
