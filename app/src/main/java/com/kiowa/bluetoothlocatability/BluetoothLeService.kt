package com.kiowa.bluetoothlocatability


import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BluetoothLeService() : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val radius  = (5.0)
    private val formulas = Formulas()
     private var hashMap  = HashMap<String,ArrayList<Double>>()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("BLE","Service accessed")
        Log.i("BLE","Attempting to start scan")


        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
                val filter = ScanFilter.Builder().setDeviceName(null).build()
                val settings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .setReportDelay(1)
                    .build()

                val filters = ArrayList<ScanFilter>()
                filters.add(filter)

                bluetoothLeScanner.startScan(filters,settings,scanCallback)

            }
        },0,3000);
        return START_STICKY
    }
    override fun onDestroy() {
        bluetoothLeScanner.stopScan(scanCallback)
        Log.i("BLE","Scan stopped ")
    }
    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.i("BLE_DETECTED",result.device.name)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            var closestVal = Double.MAX_VALUE
            var closest = ""
            for (result: ScanResult in results){
                if(result.device.name != null){
                    Log.i("BLE_DETECTED",result.device.name)
                    val distance = formulas.rssiDistanceFormula(result.rssi.toDouble(), result.txPower.toDouble())
                    if(distance<closestVal){
                        closestVal = distance
                        closest = result.device.name
                    }
                }
            }
            if(closestVal < radius){
                sendClosestDeviceBroadcast("You are in the:$closest",closest)
            }else{
                sendClosestDeviceBroadcast("Searching for nearby device....","no device found")
            }

        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("BLE_SCAN","failed")
        }
    }

    fun sendClosestDeviceBroadcast(string:String, deviceName:String){
        val intent = Intent("com.kgrjj.kiowa_daly_fyp.WithinRadius")
        intent.putExtra("com.kiowa.EXTRA_TEXT",string)
        intent.putExtra("DeviceName",deviceName)
        sendBroadcast(intent)
        Log.i("BLE","broadcast sent")
    }
    fun findClosest(){
        var closestVal = Double.MAX_VALUE
        var closest = ""

        for((k,v) in hashMap){
            val averageVal = v.average()
            if(averageVal < closestVal){
                closestVal = averageVal;
                closest = k
            }
        }
        if(closestVal < radius){
            sendClosestDeviceBroadcast("You are in the:$closest",closest)
        }else{
            sendClosestDeviceBroadcast("Searching for nearby device....","no device found")
        }
    }

}
