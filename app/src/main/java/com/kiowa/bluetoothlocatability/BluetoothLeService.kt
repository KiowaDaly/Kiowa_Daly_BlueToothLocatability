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
import com.kiowa.bluetoothlocatability.utilities.*


class BluetoothLeService : Service() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var beacons: HashMap<Int, BeaconScreenPoint>
    private lateinit var beaconHelperRoutes: BeaconShapes

    var closest = Pair(0, Double.MAX_VALUE)
    private val hashMap = HashMap<Int, ArrayList<Double>>()
    private val radius  = 6.0
    private lateinit var previousLocation: BeaconScreenPoint
    private lateinit var firstBeaconLocation: BeaconScreenPoint
    private var previousBeacon = 1





    private val aggregateRoute: ArrayList<BeaconScreenPoint> = ArrayList()

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        @Suppress("UNCHECKED_CAST")
        beacons =
            (intent?.getSerializableExtra(Constants.BEACON_MAP) as HashMap<Int, BeaconScreenPoint>)
        firstBeaconLocation =
            (intent.getSerializableExtra(Constants.FIRST_BEACON) as BeaconScreenPoint)
        //region debug messages TODO remove
        Log.i("BLE","Service accessed")
        Log.i("BLE", "Attempting to start scan")

        beaconHelperRoutes = BeaconShapes(beacons)
        for((k,v) in beacons){
            Log.i("BLE BEACONS", "Beacon" + k + " -> (" + v.x + "," + v.y + ")")
        }
        /* endregion */

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
        //simultaneously find the closest beacon
        findClosest()

        return START_STICKY
    }

    override fun onDestroy() {
        bluetoothLeScanner.stopScan(scanCallback)
        Log.i(Constants.SERVICE_TAG, "Scan stopped ")
    }



    private fun findClosest(){
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {


                for((k,v) in hashMap){
                    val averageVal = v.average()
                    if (averageVal <= closest.second) {
                        closest = closest.copy(first = k, second = averageVal)
                    }
                }
                //ensure that at least 3 beacons are found before triangulation
                if (hashMap.size == beacons.size) {
                    getLocation()
                }else{
                    Log.i(Constants.SERVICE_TAG, "Not Enough Beacons to find location!")
                }

//                if (closest.second < radius) {
//                    sendClosestDeviceBroadcast("Closest beacon ID:${closest.first}", closest.first)
//                    hashMap.clear()
//                } else {
//                    sendClosestDeviceBroadcast("Searching for nearby device....", 0)
//                }
                mainHandler.postDelayed(this, 6000)
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
        val intent = Intent(Constants.RESULTS)
//            val current = c.findCurrentPointF()
        val current = cellTower.trilaterate()
        //check if the current location is on the line
        if (!this::previousLocation.isInitialized) {
            previousLocation = firstBeaconLocation
        } else {
            for (triple in beaconHelperRoutes.getAggregateLines()) {
                if (triple.first == previousBeacon) {
                    if (triple.third.isOnApproximateLine(current)) {
                        previousLocation = current
                        aggregateRoute.add(current)
                    }
                }
            }
        }

        intent.putExtra(Constants.CURRENT_LOCATION, previousLocation)
        intent.putExtra(Constants.AGGREGATE_ROUTE, aggregateRoute)
        if (closest.second <= 6.0) {
            intent.putExtra(Constants.WITHIN_RADIUS, closest.first)
        }

        sendBroadcast(intent)
        hashMap.clear()
        Log.i(
            Constants.SERVICE_TAG,
            "broadcast sent with location : (" + current.x + "," + current.x + ")"
        )

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
                    val distance = Formulas.rssiDistanceFormula(
                        result.rssi.toDouble(),
                        result.txPower.toDouble()
                    )
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
