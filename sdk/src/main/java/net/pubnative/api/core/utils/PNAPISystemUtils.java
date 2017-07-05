// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.api.core.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

public class PNAPISystemUtils {

    private static final String TAG = PNAPISystemUtils.class.getSimpleName();
    private static String sWebViewUserAgent = null;

    /**
     * Returns the default user agent
     *
     * @return the user agentof the web view
     */
    public static String getWebViewUserAgent() {

        if (sWebViewUserAgent == null) {
            try {
                sWebViewUserAgent = System.getProperty( "http.agent" );
            } catch (Exception e) {
                Log.w(TAG, "getWebViewUserAgent - Error: cannot inject user agent");
            }
        }
        return sWebViewUserAgent;
    }

    /**
     * Returns the last known location.
     *
     * @param context valid Context
     * @return Location last registered location on device
     */
    @SuppressWarnings("MissingPermission")
    public static Location getLocation(Context context) {

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM); // Accuracy requirement.
        criteria.setSpeedRequired(true); // If speed for first location fix is required.
        String provider = locationManager.getBestProvider(criteria, true);
        Location lastKnownLocation = null;
        if (provider != null) {
            // Getting a fast fix with the last known location
            lastKnownLocation = locationManager.getLastKnownLocation(provider);
            if (lastKnownLocation == null) {
                locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
            }
        }

        return lastKnownLocation;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
