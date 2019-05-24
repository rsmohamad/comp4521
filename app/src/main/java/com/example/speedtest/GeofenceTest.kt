package com.example.speedtest

data class GeofenceTest(val name: String, val lat: Double, val long: Double, val enabled: Boolean) {
    override fun equals(other: Any?): Boolean {
        val rhs: GeofenceTest
        try {
            rhs = (other as GeofenceTest)
        } catch (e: Exception) {
            return false
        }
        return rhs.name == name && rhs.lat == lat && rhs.long == long
    }
}