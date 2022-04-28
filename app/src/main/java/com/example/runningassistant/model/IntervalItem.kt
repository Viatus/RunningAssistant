package com.example.runningassistant.model

class IntervalItem(var intervalType: Int = INTERVAL_TYPE_SLOW,
                   var intervalMeasurementType:Int = INTERVAL_MEASUREMENT_TIME,
                   var goal:Float = 0.0F
) {

    companion object {
        const val INTERVAL_TYPE_SLOW = 0
        const val INTERVAL_TYPE_NORMAL = 1
        const val INTERVAL_TYPE_FAST = 2

        const val INTERVAL_MEASUREMENT_TIME = 0
        const val INTERVAL_MEASUREMENT_DISTANCE = 1
    }
}