package com.kiowa.bluetoothlocatability

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.kiowa.bluetoothlocatability.triangulationMethods.CellTowerTrilateration
import com.kiowa.bluetoothlocatability.utilities.BeaconData
import com.kiowa.bluetoothlocatability.utilities.BeaconScreenPoint


class BluetoothLeService : Service() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val radius  = 6.0
    private val formulas = Formulas()
    private var hashMap  = HashMap<Int,ArrayList<Double>>()
    private lateinit var beacons: HashMap<Int, BeaconScreenPoint>
    private val aggregateRoute: ArrayList<BeaconScreenPoint> = ArrayList()

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        @Suppress("UNCHECKED_CAST")
        beacons = (intent?.getSerializableExtra("Beacons") as HashMap<Int, BeaconScreenPoint>)
        //region debug messages TODO remove
        Log.i("BLE","Service accessed")
        Log.i("BLE", "Attempting to start scan")


        for((k,v) in beacons){
            Log.i("BLE BEACONS", "Beacon" + k + " -> (" + v.x + "," + v.y + ")")
        }
        /* endregion */

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        //set the scan to have a delay of 1second - ie the scan refreshes every 1 second
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setReportDelay(1)
            .build()

        //set the scan to only find devices with the following names
        val filters = arrayListOf<ScanFilter>(
            ScanFilter.Builder().setDeviceName("1").build(),
            ScanFilter.Builder().setDeviceName("2").build(),
            ScanFilter.Builder().setDeviceName("3").build(),
            ScanFilter.Builder().setDeviceName("4").build(),
            ScanFilter.Builder().setDeviceName("5").build()
        )

        //begin scanning
        bluetoothLeScanner.startScan(filters,settings,scanCallback)
        findClosest()

        return START_STICKY
    }

    override fun onDestroy() {
        bluetoothLeScanner.stopScan(scanCallback)
        Log.i("BLE","Scan stopped ")
    }



    private fun findClosest(){
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {

                var closest = Pair<Int, Double>(0, Double.MAX_VALUE)
                for((k,v) in hashMap){
                    val averageVal = v.average()
                    if (averageVal <= closest.second) {
                        closest = closest.copy(first = k, second = averageVal)
                    }
                }
                //ensure that at least 3 beacons are found before triangulation
                if (hashMap.size >= 3) {
                    getLocation()
                }else{
                    Log.i("BLE","Not Enough Beacons to find location!")
                }

                if (closest.second < radius) {
                    sendClosestDeviceBroadcast("Closest beacon ID:$closest", closest.first)
                    hashMap.clear()
                }else{
                    sendClosestDeviceBroadcast("Searching for nearby device....",0)
                }
                mainHandler.postDelayed(this, 3000)
            }
        })
    }

    private fun getLocation(){
        val beaconData = ArrayList<BeaconData>()
        //added this for change
            for((k,v) in hashMap){
                if (beacons.containsKey(k)) {
                    beaconData.add(BeaconData(k, beacons[k]!!, v.average()))
                }
            }
//            val c = Centroid(pairs)
        val cellTower = CellTowerTrilateration(beaconData)
            val intent = Intent("com.kgrjj.kiowa_daly_fyp.CurrentLocation")
//            val current = c.findCurrentPointF()
            val current = cellTower.trilaterate()
            aggregateRoute.add(current)
            intent.putExtra("com.kiowa.CURRENT_LOCATION",current)
            sendBroadcast(intent)
        Log.i("BLE", "broadcast sent with loaction : (" + current.x + "," + current.x + ")")

    }


    private fun sendClosestDeviceBroadcast(string:String, deviceName:Int){
        val intent = Intent("com.kgrjj.kiowa_daly_fyp.WithinRadius")
        intent.putExtra("com.kiowa.EXTRA_TEXT",string)
        intent.putExtra("DeviceName",deviceName)
        sendBroadcast(intent)
        Log.i("BLE","broadcast sent")
    }




    // Callbacks

    /**
     *
     */
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
                val n = result.device.name
                if(n != null){
                    val name = n.toInt()
                    Log.i("BLE_DETECTED","Beacon" + name)
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

    /**
     *
     */


}
