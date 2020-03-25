package com.kiowa.bluetoothlocatability

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kiowa.bluetoothlocatability.triangulationMethods.CellTowerTrilateration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BluetoothLeService : Service() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val radius  = (5.0)
    private val formulas = Formulas()
    private var hashMap  = HashMap<String,ArrayList<Double>>()
    private lateinit var beacons : HashMap<String,FloatArray>
    private val aggregateRoute : ArrayList<FloatArray> = ArrayList()

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        @Suppress("UNCHECKED_CAST")
        beacons = (intent?.getSerializableExtra("Beacons") as HashMap<String, FloatArray>)

        Log.i("BLE","Service accessed")
        Log.i("BLE", "Attempting to start scan")

        for((k,v) in beacons){
            Log.i("BLE BEACONS",k + " -> (" + v[0] + "," + v[1]+")")
        }

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
        findClosest()

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

        /**
         * Function retrieves scan results, makes sure that they don't have null names
         * and then stores them in a hashmap with the distance as value
         */
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (result: ScanResult in results){
                val name = result.device.name
                if(name != null){
                    Log.i("BLE_DETECTED",name)
                    val distance = formulas.rssiDistanceFormula(result.rssi.toDouble(), result.txPower.toDouble())
                    if(!hashMap.containsKey(name)){
                        hashMap[name] = ArrayList()
                    }
                    hashMap[name]?.add(distance)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("BLE_SCAN","failed")
        }
    }

    private fun findClosest(){
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                var closestVal = Double.MAX_VALUE
                var closest = ""

                for((k,v) in hashMap){
                    val averageVal = v.average()
                    if(averageVal < closestVal){
                        closestVal = averageVal
                        closest = k
                    }
                }
                if(hashMap.size == beacons.size){
                    getLocation()
                }else{
                    Log.i("BLE","Not Enough Beacons to find location!")
                }

                if(closestVal < radius){
                    sendClosestDeviceBroadcast("You are in the:$closest",closest)
                    hashMap.clear()
                }else{
                    sendClosestDeviceBroadcast("Searching for nearby device....","no device found")
                }
            }
        },0,2000)
    }

    private fun getLocation(){
        val pairs = HashMap<String,Pair<FloatArray,Double>>()

            for((k,v) in hashMap){
                if(beacons.containsKey(k)) pairs[k] = Pair(beacons[k]!!,v.average())
            }
//            val c = Centroid(pairs)
            val cellTower = CellTowerTrilateration(pairs)
            val intent = Intent("com.kgrjj.kiowa_daly_fyp.CurrentLocation")
//            val current = c.findCurrentPointF()
            val current = cellTower.trilaterate()
            aggregateRoute.add(current)
            intent.putExtra("com.kiowa.CURRENT_LOCATION",current)
            sendBroadcast(intent)
            Log.i("BLE","broadcast sent with loaction : ("+ current[0] +","+current[1]+")")

    }

    private fun sendClosestDeviceBroadcast(string:String, deviceName:String){
        val intent = Intent("com.kgrjj.kiowa_daly_fyp.WithinRadius")
        intent.putExtra("com.kiowa.EXTRA_TEXT",string)
        intent.putExtra("DeviceName",deviceName)
        sendBroadcast(intent)
        Log.i("BLE","broadcast sent")
    }

}
