package com.kiowa.bluetoothlocatability.testCode

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kiowa.bluetoothlocatability.utilities.Constants
import com.kiowa.bluetoothlocatability.utilities.Formulas

class TestService : Service() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val hashMap = HashMap<Int, ArrayList<Double>>()


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        val closestID = intent!!.getIntExtra("correct_device", 0)
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setReportDelay(1)
            .build()

        //set the scan to only find devices with the following names
        val filters = arrayListOf<ScanFilter>(
            ScanFilter.Builder().setDeviceName("1").build()
        )

        //begin scanning
        bluetoothLeScanner.startScan(filters, settings, scanCallback)
        //simultaneously find the closest beacon
        return START_STICKY
    }

    override fun onDestroy() {
        bluetoothLeScanner.stopScan(scanCallback)
        val intent = Intent("DISTANCE")
        intent.putExtra("distance", hashMap[1]!!.average())
        sendBroadcast(intent)
        Log.i(Constants.SERVICE_TAG, "Scan stopped ")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.i("BLE_DETECTED", result.device.name)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (result: ScanResult in results) {
                val n = result.device.name
                if (n != null) {
                    val name = n.toInt()
                    Log.i("BLE_DETECTED", "Beacon$name")
                    val distance = Formulas.rssiDistanceFormula(
                        result.rssi.toDouble(),
                        result.txPower.toDouble()
                    )
                    if (!hashMap.containsKey(name)) {
                        hashMap[name] = ArrayList()
                    }
                    hashMap[name]?.add(distance)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("BLE_SCAN", "failed")
        }
    }

}