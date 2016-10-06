// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
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

package net.pubnative.mediation.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.util.Log;

public class PubnativeDeviceUtils {

    private static String TAG = PubnativeDeviceUtils.class.getSimpleName();

    /**
     * Gets you the PackageInfo object based on the Context object passed in.
     *
     * @param context valid context object.
     * @return PackageInfo object if context is valid, else null
     */
    public static PackageInfo getPackageInfo(Context context) {

        Log.v(TAG, "getPackageInfo(Context context)");

        PackageInfo result = null;
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            Log.e(TAG, "getPackageInfo - Error:" + e);
        }
        return result;
    }

    /**
     * Gets you the android advertising id.
     * Note: This method should be called from a non-UI thread.
     *
     * @param context valid Context object
     * @return android advertising id if available, else null.
     */
    public static String getAndroidAdvertisingID(Context context) {

        Log.v(TAG, "getAndroidAdvertisingID(Context context)");

        AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);

        String androidAdvertisingID = null;
        if (adInfo.isLimitAdTrackingEnabled()) {
            Log.w(TAG, "getAndroidAdvertisingID  - Error: limit ad tracking is enabled, android advertising id cannot be retrieved");
        } else {
            androidAdvertisingID = adInfo.getId();
        }
        return androidAdvertisingID;
    }

    /**
     * Checks if the current network is available and connected to internet
     * @param context
     * @return true if it's available and connected
     */
    public static boolean isNetworkAvailable(Context context) {

        Log.v(TAG, "isNetworkAvailable(Context context)");

        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
