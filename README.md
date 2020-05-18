# Kiowa_Daly_BlueToothLocatability
Kiowa Dalys Final Year Prject

---

## **Integrating with an application**

Within your build.gradle file insert the following 

    implementation 'com.github.KiowaDaly:Kiowa_Daly_BlueToothLocatability:v1.0'
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.3.72"
    
    Additionlly you must integrate a broadcast reviever in your application that litens for 
        1. Constants.CURRENT_LOCATION
        2. Constants.AGGREGATE_ROUTE
        3. Constants.WITHIN_RADIUS
    
    the results found from these broadcasts recieved are to be used by your disgretion.

### **Calling on the service**

To start the bacgkround service ensure that you have a HashMap<Integer,BeaconScreenPoint> that stores the coordinates of each beacon and an integer denoting the beacon ID.


Have a list of the devices names, which will be used for filtering results during a scan.


Call upon the static function: 
         UserLocationCapability.Companion.startSystem(context,HashMap, ClosestBeaconScreenPoint,device_names_list);
