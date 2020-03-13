package com.kiowa.bluetoothlocatability

import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.round

class Formulas {
    fun rssiDistanceFormula(rssi: Double, P: Double): Double {
        //d = 10 ^ ((P-Rssi) / 10n) (n ranges from 2 to 4)
        val n = 2
        val bottom = (10.0 * n)
        val top = (P - (rssi))
        val powVal = (top/bottom)
        val value =  ((10.0).pow(powVal))
        return value / 10.0.pow(9.0)
    }
}
