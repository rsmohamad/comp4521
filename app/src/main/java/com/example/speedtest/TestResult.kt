package com.example.speedtest


data class TestResult(
    val downloadRate: Double?,
    val uploadRate: Double?,
    val locationX: Double?,
    val locationY: Double?,
    val locationZ: Double?,
    val timestamp: Long?,
    val provider: String?,
    val planMbps: String?,
    val imei: String?,
    val isWifi: Boolean?
) {

    fun toMap(): HashMap<String, Any?> {
        val testResult = HashMap<String, Any?>()
        testResult["download_rate"] = downloadRate
        testResult["upload_rate"] = uploadRate
        testResult["location_x"] = locationX
        testResult["location_y"] = locationY
        testResult["location_z"] = locationZ
        testResult["timestamp"] = System.currentTimeMillis()
        testResult["provider"] = provider
        testResult["plan_mbps"] = ""
        testResult["imei"] = imei
        testResult["is_wifi"] = isWifi
        return testResult
    }

    companion object {
        fun fromMap(testResult: Map<String, Any?>): TestResult {
            return TestResult(
                testResult["download_rate"] as Double?,
                testResult["upload_rate"] as Double?,
                testResult["location_x"] as Double?,
                testResult["location_y"] as Double?,
                testResult["location_z"] as Double?,
                testResult["timestamp"] as Long?,
                testResult["provider"] as String?,
                testResult["plan_mbps"] as String?,
                testResult["imei"] as String?,
                testResult["is_wifi"] as Boolean?
            )
        }
    }

}