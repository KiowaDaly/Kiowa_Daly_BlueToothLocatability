package com.kiowa.bluetoothlocatability.utilities

class BeaconShapes(beacons: HashMap<Int, BeaconScreenPoint>) {
    private val aggregateLines: ArrayList<Triple<Int, Int, Line>> = ArrayList()

    init {
        val myList = beacons.toList().sortedBy { it.first }
        for (i in myList.indices) {
            if (i == myList.size - 1) {
                val one = myList[i].first
                val two = myList[0].first
                aggregateLines.add(
                    Triple(
                        one, two,
                        Formulas.getLine(
                            beacons[one]!!,
                            beacons[two]!!
                        )
                    )
                )
            } else {
                val one = myList[i].first
                val two = myList[i + 1].first
                aggregateLines.add(
                    Triple(
                        one, two,
                        Formulas.getLine(
                            beacons[one]!!,
                            beacons[two]!!
                        )
                    )
                )
            }

        }
    }

    fun getAggregateLines(): ArrayList<Triple<Int, Int, Line>> {
        return aggregateLines
    }
}