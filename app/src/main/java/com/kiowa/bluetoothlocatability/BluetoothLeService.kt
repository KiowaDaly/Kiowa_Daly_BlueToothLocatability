package com.kiowa.bluetoothlocatability

import android.app.IntentService
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Bundle
import android.util.Log

class BluetoothLeService(name: String?) : IntentService(name) {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val formulas = Formulas()

    override fun onHandleIntent(intent: Intent?) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        bluetoothLeScanner.startScan(scanCallback)
        Log.i("BLE","HANDLEiNTENT")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            val n = 4
//            var distance = 0.0
//            for (i in 0 until n) {
//                val rssi = result.rssi.toDouble()
//                val measuredPower = result.txPower.toDouble()
//                distance += formulas.rssiDistanceFormula(rssi, measuredPower)
//            }
//            distance /= n
//            val myBroadcast = Intent()
//            myBroadcast.action = "DEVICE NEARBY!"
//            val bundle = Bundle()
//            bundle.putDouble("Distance", distance)
//            bundle.putString("Beacon Name", result.device.name)
//            myBroadcast.putExtra("Distance", distance)
//            sendBroadcast(myBroadcast)
            Log.i("BLE_DETECTED",result.device.name)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            //Do the triangulation in here! and return the closest device
            for (result in results){
                Log.i("BLE_DETECTED",result.device.name)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("BLE_SCAN","failed")
        }
    }

    /**
     * The required methods for a service
     *
     *
     *
     *
     *
     */

}
