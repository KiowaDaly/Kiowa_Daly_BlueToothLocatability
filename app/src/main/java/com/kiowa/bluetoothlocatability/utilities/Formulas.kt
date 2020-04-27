package com.kiowa.bluetoothlocatability.utilities

import kotlin.math.pow

class Formulas {
    companion object {
        /**
         * rssiDistanceFormula calculates the estimated distance the device is from bluetooth low energy beacons
         *
         * variable n = environmental factor
         * @param rssi
         * @param txPower
         * @author Kiowa Daly
         * @return Double
         */
        fun rssiDistanceFormula(rssi: Double, txPower: Double): Double {
            //d = 10 ^ ((txPower-Rssi) / 10n) (n ranges from 2 to 4)
            val n = 2
            val bottom = (10.0 * n)
            val top = (txPower - (rssi))
            val powVal = (top / bottom)
            val value = ((10.0).pow(powVal))
            //divide by 10^9 otherwise we get a massive number
            return value / 10.0.pow(9.0)
        }

        private fun getSlope(a: BeaconScreenPoint, b: BeaconScreenPoint): Double {
            return (b.y - a.y) / (b.x - a.x)
        }

        fun getLine(a: BeaconScreenPoint, b: BeaconScreenPoint): Line {
            val m =
                getSlope(
                    a,
                    b
                )
            val c = a.y - (m * a.x)
            return Line(m, c, a, b)
        }

    }


}
