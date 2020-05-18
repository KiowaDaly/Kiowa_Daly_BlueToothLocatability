# Kiowa_Daly_BlueToothLocatability
Kiowa Dalys fFinal Year Prject

---

## **Integrating with an application**

Within your build.gradle file insert the following 
---
    implementation 'com.github.KiowaDaly:Kiowa_Daly_BlueToothLocatability:v1.0'
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.3.72"
    
    Additionlly you must integrate a broadcast reviever in your application that litens for 
        * Constants.CURRENT_LOCATION
        * Constants.AGGREGATE_ROUTE
        * Constants.WITHIN_RADIUS
    
    the results found from these broadcasts recieved are to be used by your disgretion.
