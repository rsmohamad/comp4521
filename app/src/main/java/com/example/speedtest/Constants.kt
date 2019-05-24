package com.example.speedtest

import java.util.HashMap

/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.google.android.gms.maps.model.LatLng


/**
 * Constants used in this sample.
 */

internal object Constants {

    private val PACKAGE_NAME = "com.google.android.gms.location.Geofence"

    val GEOFENCES_ADDED_KEY = "$PACKAGE_NAME.GEOFENCES_ADDED_KEY"

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private val GEOFENCE_EXPIRATION_IN_HOURS: Long = 12

    /**
     * For this sample, geofences expire after twelve hours.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS =
        GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000
    val GEOFENCE_RADIUS_IN_METERS = 1609f * 5 // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    val HKLOCATIONS: HashMap<String, LatLng>
        get() {
            val rv = HashMap<String, LatLng>()
            rv["HKUST"] = LatLng(22.337690, 114.265466)
            rv["CHOIHUNG"] = LatLng(22.334886, 114.208981)
            rv["HANGHAU"] = LatLng(22.315605, 114.264416)
            return rv
        }

}
